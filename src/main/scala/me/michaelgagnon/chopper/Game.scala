package me.michaelgagnon.chopper

// TODO: rm
import com.scalawarrior.scalajs.createjs

import org.querki.jquery._
import scala.collection.mutable

class Game(val viz: Viz, val level: Level) {

  val controller = new Controller()

  val droneVizElement: VizElement[DroneElement] = viz.getDroneVizElement(level)
  droneVizElement.addToStage(viz.stage)
  viz.camera.setCanvasXy(droneVizElement)

  // TODO: get ground elements, get fire elements, etc.
  // TODO: maybe store vizElements in viz?
  val vizElements: Seq[VizElement[_ <: GameElement]] = viz.getVizElements(level)
  viz.addElementsToStage(vizElements)

  val waterElements: mutable.ListBuffer[VizElement[WaterElement]] = mutable.ListBuffer(vizElements: _*).flatMap {
      case BitmapVizElement(bitmap, water: WaterElement) => Some(BitmapVizElement(bitmap, water))
      case _ => None
    }

  var lastWaterTimestamp = System.currentTimeMillis() - WaterElement.interDelay

  def tick() {
    if (controller.paused) return

    // TODO: refactor
    if (Controller.keyPressed(KeyCode.Space) && System.currentTimeMillis() - lastWaterTimestamp > WaterElement.interDelay) {
      lastWaterTimestamp = System.currentTimeMillis()
      val dv = droneVizElement.gameElement.velocity
      val dcp = droneVizElement.gameElement.currentPosition
      // TODO: abstraction violations
      val waterElement = WaterElement(Xy(dcp.x, dcp.y))
      val waterViz = BitmapVizElement(new createjs.Bitmap(viz.image.water), waterElement)
      waterElements.append(waterViz)
      //viz.stage.addChild(newWater.ble.bitmap)
      viz.camera.setCanvasXy(waterViz)
      waterViz.addToStage(viz.stage)
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
    waterElements.foreach(_.gameElement.updateState(Xy(0.0, 0.0), level.elements))
    // TODO: figure out what to hoist into Viz
    viz.camera.positionCamera(droneVizElement)
    viz.updateCanvasCoodrinates(droneVizElement)
    vizElements.foreach(viz.updateCanvasCoodrinates(_))
    waterElements.foreach(viz.updateCanvasCoodrinates(_))
    viz.updateBackground()
    viz.stage.update()
  }

}