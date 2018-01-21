package me.michaelgagnon.chopper

import org.querki.jquery._

class Game(val viz: Viz, val level: Level) {

  val controller = new Controller()

  val droneVizElement: VizElement[DroneElement] = viz.getDroneVizElement(level)
  droneVizElement.addToStage(viz.stage)
  viz.camera.setCanvasXy(droneVizElement)

  // TODO: get ground elements, get fire elements, etc.
  val vizElements: Seq[VizElement[_ <: GameElement]] = viz.getVizElements(level)
  viz.addElementsToStage(vizElements)

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
    viz.camera.positionCamera(droneVizElement)
    viz.updateCanvasCoodrinates(droneVizElement)
    vizElements.foreach(viz.updateCanvasCoodrinates(_))
    viz.stage.update()
  }

}