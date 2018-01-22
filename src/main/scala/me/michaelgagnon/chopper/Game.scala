package me.michaelgagnon.chopper

import org.querki.jquery._
import scala.collection.mutable

class Game(val level: Level, val gameId: String, val image: Image) {

  val viz = new Viz(level, gameId, image)
  val controller = new Controller()

  val droneVizElement: VizElement[DroneElement] = viz.getDroneVizElement()
  val fireVizElements: Seq[VizElement[FireElement]] = viz.getFireElements(level)
  val groundVizElements: Seq[VizElement[GroundElement]] = viz.getGroundElements(level)
  val waterVizElements = mutable.ListBuffer[VizElement[WaterElement]]()

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
      val waterVizElement = viz.newWaterVizElement(waterElement)
      waterVizElements.append(waterVizElement)
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

    waterVizElements.foreach{ w =>
      val result = w.gameElement.updateState(Xy(0.0, 0.0), level.groundElements)
      if (result == FlyResult.Collision) {
        
        /*val fireElements = vizElements.filter {
          case SpriteVizElement(sprite, FireElement(orig)) => 
        }*/

        /*vizElements
          .foreach {
              case SpriteVizElement(_, FireElement(orig)) => {

              }
              case _=> false
            
          }
        */

      }
    }

    viz.update(droneVizElement, fireVizElements, groundVizElements, waterVizElements)
  }

}