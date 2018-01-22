package me.michaelgagnon.chopper

import scala.collection.mutable

case class Explosion(vizElement: VizElement[ExplosionElement], timestamp: Double)

class Game(val level: Level, val gameId: String, val image: Image) {

  val viz = new Viz(level, gameId, image)
  val controller = new Controller()

  val fireVizElements: Seq[VizElement[FireElement]] = viz.getFireElements(level)
  val groundVizElements: Seq[VizElement[GroundElement]] = viz.getGroundElements(level)
  var waterVizElements = List[VizElement[WaterElement]]()
  val droneVizElement: VizElement[DroneElement] = viz.getDroneVizElement()

  // TODO: document
  var lastWaterTimestamp = System.currentTimeMillis() - WaterElement.interDelay

  // When an explosion occurs, then explosion is set to Some(Explosion)
  var explosion: Option[Explosion] = None

  def waterAvailableForDrop() = System.currentTimeMillis() - lastWaterTimestamp > WaterElement.interDelay


  def tick() {
    if (controller.paused) return

    // This is low level viz stuff, but this seems the simplest place to put the code.
    // During more proper MVC separation would seem to unnecessarily obfuscate the code
    explosion match {
      case None => ()
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
        0.0
      }

    val thrustX =
      if (Controller.keyPressed(KeyCode.Left)) {
        -5.0
      } else if (Controller.keyPressed(KeyCode.Right)) {
        5.0
      } else {
        0.0
      }

    val droneResult = if (explosion.isEmpty) {
      droneVizElement.gameElement.updateState(Xy(thrustX, thrustY), level)
    } else {
      FlyResult.StillFlying
    }

    processDroneResult(droneResult)

    waterVizElements = processWaterElements()

    viz.update(droneVizElement, fireVizElements, groundVizElements, waterVizElements)
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