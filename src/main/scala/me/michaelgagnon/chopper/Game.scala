package me.michaelgagnon.chopper

import org.querki.jquery._
import scala.collection.mutable

class Game(val level: Level, val gameId: String, val image: Image) {

  val viz = new Viz(level, gameId, image)
  val controller = new Controller()

  val droneVizElement: VizElement[DroneElement] = viz.getDroneVizElement()
  val fireVizElements: Seq[VizElement[FireElement]] = viz.getFireElements(level)
  val groundVizElements: Seq[VizElement[GroundElement]] = viz.getGroundElements(level)
  var waterVizElements = List[VizElement[WaterElement]]()

  // TODO: document
  var lastWaterTimestamp = System.currentTimeMillis() - WaterElement.interDelay

  def waterAvailableForDrop() = System.currentTimeMillis() - lastWaterTimestamp > WaterElement.interDelay

  def tick() {
    if (controller.paused) return

    // TODO: refactor
    if (Controller.keyPressed(KeyCode.Space) && waterAvailableForDrop()) {
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

    // TODO: detect crashes and oob
    // REFACTOR
    val droneResult = droneVizElement.gameElement.updateState(Xy(thrustX, thrustY), level.groundElements)

    if (droneResult == FlyResult.OutOfBounds) {
      waterVizElements.foreach(viz.removeWaterVizElement(_))
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

    waterVizElements = waterVizElements.filter { w =>
      val result = w.gameElement.updateState(Xy(0.0, 0.0), level.groundElements)
      if (result != FlyResult.Collision) {
        true
      } else {
      
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
              f.gameElement.currentPosition.y = -10000.0
            }
          }

          viz.removeWaterVizElement(w)
          false
      }
    }

    viz.update(droneVizElement, fireVizElements, groundVizElements, waterVizElements)
  }

}