package me.michaelgagnon.chopper

import org.querki.jquery._

class Game(val viz: Viz, val level: Level) {

  val droneVizElement: VizElement = viz.getDroneVizElement(level)
  val vizElements: Seq[VizElement] = viz.getVizElements(level)
  viz.addElementsToStage(vizElements)

  def tick() {
      viz.stage.update()
  }

}