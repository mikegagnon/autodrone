package me.michaelgagnon.chopper

import org.querki.jquery._

class Game(val viz: Viz, val level: Level) {

  val controller = new Controller()
  val droneVizElement: VizElement = viz.getDroneVizElement(level)
  val vizElements: Seq[VizElement] = viz.getVizElements(level)
  viz.addElementsToStage(vizElements)

  viz.camera.setCanvasXy(droneVizElement)
  droneVizElement.addToStage(viz.stage)

  def tick() {

    if (controller.paused) return

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

    droneVizElement.gameElement.updateState(Xy(thrustX, thrustY))

    viz.stage.update()
  }

}