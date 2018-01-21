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
    viz.stage.update()
  }

}