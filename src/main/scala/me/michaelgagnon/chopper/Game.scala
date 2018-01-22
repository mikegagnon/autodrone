package me.michaelgagnon.chopper

// TODO: rm
import com.scalawarrior.scalajs.createjs

import org.querki.jquery._
import scala.collection.mutable

//class Game(val viz: Viz, val level: Level) {
class Game(val level: Level, val gameId: String, val image: Image) {

  val viz = new Viz(level, gameId, image)
  val controller = new Controller()

  val droneVizElement: VizElement[DroneElement] = viz.getDroneVizElement()

  // TODO: get ground elements, get fire elements, etc.
  // TODO: maybe store vizElements in viz?
  val vizElements: Seq[VizElement[_ <: GameElement]] = viz.getVizElements(level)

  // TODO: document
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

    droneVizElement.gameElement.updateState(Xy(thrustX, thrustY), level.elements)
    waterVizElements.foreach(_.gameElement.updateState(Xy(0.0, 0.0), level.elements))

    viz.update(droneVizElement, vizElements, waterVizElements)
  }

}