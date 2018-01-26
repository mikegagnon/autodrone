package me.michaelgagnon.chopper

import scala.collection.mutable

case class Explosion(vizElement: VizElement[ExplosionElement], timestamp: Double)

// Every tick, resting is updated such that
// numTicks is the number of ticks that the drone has been resting at position with no thrust
// Used to help determine victory condition
class Resting(var position: Xy, var numTicks: Int)

class Game(val level: Level, val gameId: String, val image: Image) {

  val viz = new Viz(level, gameId, image)
  val controller = new Controller(gameId)
  val interpreter = new Interpreter() //new State(mutable.HashMap("altitude", Variable("altitude", METERS, 0.0))))

  val fireVizElements: Seq[VizElement[FireElement]] = viz.getFireElements(level)
  val groundVizElements: Seq[VizElement[GroundElement]] = viz.getGroundElements(level)
  var waterVizElements = List[VizElement[WaterElement]]()
  val droneVizElement: VizElement[DroneElement] = viz.getDroneVizElement()

  // TODO: document
  var lastWaterTimestamp = System.currentTimeMillis() - WaterElement.interDelay

  // When an explosion occurs, then explosion is set to Some(Explosion)
  var explosion: Option[Explosion] = None

  def waterAvailableForDrop() = System.currentTimeMillis() - lastWaterTimestamp > WaterElement.interDelay

  val resting = new Resting(Xy(-100.0, -100.0), 0)

  var victory = false

  def runProgram(): State = {
    interpreter.state.variables("altitude") = Variable("altitude", METERS, droneVizElement.gameElement.altitude)
    val text = Global.currentEditor.get.getDoc().getValue()
    println(text)
    val program = Compiler(text) match {
      case Left(error) => println(error)
      case Right(p) => {
        try {
          interpreter.run(p)
        } catch {
          case InterpreterCrash(message) => println(message)
        }
      }
    }
    println(interpreter.state.variables)
    interpreter.state
  }

  /*
  if (altitude < 3 meters) {
  up = 20.0 meters
} else if (altitude < 4 meters) {
  up = 15.0 meters
} else if (altitude < 5 meters) {
  up = 10.0 meters
} else if (altitude < 10 meters) {
  up = 10 meters
} else {
  up = 9.75 meters
}



if (altitude < 5 meters) {
  up = 10.0 thrust
} else {
  up = 9.75 thrust
}
*/

  def tick() {
    if (controller.paused) return

    val state: State = runProgram()

    val thrustUp: Double = state.variables.get("up").map(_.value).getOrElse(0.0)

    println(thrustUp)

    // This is low level viz stuff, but this seems the simplest place to put the code.
    // During more proper MVC separation would seem to unnecessarily obfuscate the code
    explosion match {
      case None => {
        val drone = droneVizElement.gameElement.currentPosition
        if (drone.x == resting.position.x && drone.y == resting.position.y) {
          resting.numTicks += 1
        } else {
          resting.position.x = drone.x
          resting.position.y = drone.y
          resting.numTicks = 0
        }
      }
      case Some(Explosion(vizElement, t)) =>
        if (System.currentTimeMillis() - t > Viz.explosionDuration) {
          // The explosion is over
          explosion = None
          viz.removeVizElement(vizElement)
          resetLevel()
        }
    }

    // TODO: refactor
    if (explosion.isEmpty && Controller.keyPressed(KeyCode.Space) && waterAvailableForDrop()) {
      lastWaterTimestamp = System.currentTimeMillis()
      val dv = droneVizElement.gameElement.velocity
      val dcp = droneVizElement.gameElement.currentPosition
      // TODO: abstraction violations
      val waterElement = WaterElement(Xy(dcp.x, dcp.y))
      waterElement.velocity.x = dv.x 
      waterElement.velocity.y = dv.y

      val waterVizElement = viz.newWaterVizElement(waterElement)
      waterVizElements = waterVizElement :: waterVizElements
    }

    val thrustY =
      if (Controller.keyPressed(KeyCode.Up)) {
        -20.0
      } else {
        -thrustUp
      }

    val thrustX =
      if (Controller.keyPressed(KeyCode.Left)) {
        -5.0
      } else if (Controller.keyPressed(KeyCode.Right)) {
        5.0
      } else {
        0.0
      }

    if (thrustX != 0.0 || thrustY != 0.0) {
      resting.numTicks = 0
    }

    val droneResult = if (explosion.isEmpty && !victory) {
      droneVizElement.gameElement.updateState(Xy(thrustX, thrustY), level)
    } else {
      FlyResult.StillFlying
    }

    processDroneResult(droneResult)

    waterVizElements = processWaterElements()

    checkVictory()

    viz.update(droneVizElement, fireVizElements, groundVizElements, waterVizElements)
  }

  def checkVictory() {
    if (resting.numTicks >= 10 && !fireVizElements.exists(_.gameElement.currentPosition.x >= 0.0)) {
      victory = true
      viz.youwin()
    }
  }

  def resetLevel() = {
    waterVizElements.foreach(viz.removeVizElement(_))
    waterVizElements = Nil
    fireVizElements.foreach { f =>
      f.gameElement.currentPosition.x = f.gameElement.origPosition.x
      f.gameElement.currentPosition.y = f.gameElement.origPosition.y
    }

    droneVizElement.gameElement.currentPosition.x = droneVizElement.gameElement.origPosition.x
    droneVizElement.gameElement.currentPosition.y = droneVizElement.gameElement.origPosition.y
    droneVizElement.gameElement.velocity.x = 0.0
    droneVizElement.gameElement.velocity.y = 0.0

    viz.reset()
  }

  def processDroneResult(droneResult: FlyResult.EnumVal) =
    droneResult match {
      case FlyResult.OutOfBounds => resetLevel()
      case FlyResult.FireCollision => newExplosion()
      case FlyResult.GroundCollision(velocity) => {
        val maxVelocity = Math.max(Math.abs(velocity.x), Math.abs(velocity.y))
        if (maxVelocity > DroneElement.fastestSafeVelocity) {
          newExplosion()
        }
      }
      case FlyResult.StillFlying => ()
    }

  def newExplosion() {
    val vizElement = viz.newExplosionElement(Xy(droneVizElement.gameElement.currentPosition.x, droneVizElement.gameElement.currentPosition.y))
    explosion = Some(Explosion(vizElement, System.currentTimeMillis()))
    droneVizElement.gameElement.currentPosition.x = -10000.0
  }

  def processWaterElements() = waterVizElements.filter { w =>
      val result = w.gameElement.updateState(Xy(0.0, 0.0), level)

      result match {
        case FlyResult.GroundCollision(_) => {
          fireVizElements.foreach { f: VizElement[FireElement] =>
            val wx = w.gameElement.currentPosition.x
            val wy = w.gameElement.currentPosition.y
            val fx = f.gameElement.currentPosition.x
            val fyTop = f.gameElement.currentPosition.y + FireElement.coreHeight
            val fyBottom = f.gameElement.currentPosition.y + FireElement.dim.y

            // If the water hit the fire
            if (Math.abs(wx - fx) < FireElement.dim.x / 2.0 && wy > fyTop && wy < fyBottom) {
              // Move the water out of bounds
              f.gameElement.currentPosition.x = -10000.0
            }
          }

          viz.removeVizElement(w)
          false
        }
        case _ => true
      }
  }

}