package me.michaelgagnon.chopper

import com.scalawarrior.scalajs.createjs
import org.querki.jquery._
import scala.scalajs.js

sealed abstract class VizElement {
  val gameElement: GameElement
  // Set the canvas coordinates for this VizElement
  def setXy(xy: Xy): Unit
}

case class BitmapElement(bitmap: createjs.Bitmap, gameElement: GameElement) extends VizElement {
  def setXy(xy: Xy) = {
    bitmap.x = xy.x
    bitmap.y = xy.y
  }
}

case class SpriteElement(sprite: createjs.Sprite, gameElement: GameElement) extends VizElement {
  def setXy(xy: Xy) = {
    sprite.x = xy.x
    sprite.y = xy.y
  }
}

object Viz {
  val fps = 30.0
}

class Viz(val id: String, val image: Image) {

  val div = $(s"#$id")

  val canvasId = id + "canvas"

  val stage = new createjs.Stage(canvasId)

  val canvas = div.find("canvas")

  canvas.attr("id", canvasId)

  val canvasSize = Xy(
    canvas.attr("width").get.toDouble,
    canvas.attr("height").get.toDouble)

  val camera = new Camera(canvasSize)

  val fireSpriteSheet = new createjs.SpriteSheet(
    js.Dictionary(
      "images" -> js.Array(image.fire),
      "frames" -> js.Dictionary(
        "width" -> FireElement.dim.x,
        "height" -> FireElement.dim.y,
        "regX" -> 0,
        "regY" -> 0
      ),
      "animations" -> js.Dictionary(
        "walk" -> js.Array(0, 1, "walk")
      )
    )
  )

  def groundDirectionToImage(ground: GroundElement) =
    ground.direction match {
      case GroundElement.TopCenter => image.groundTopCenter
      case GroundElement.TopLeft => image.groundTopLeft
      case GroundElement.TopRight => image.groundTopRight
      case GroundElement.BottomCenter => image.groundBottomCenter
      case GroundElement.BottomLeft => image.groundBottomLeft
      case GroundElement.BottomRight => image.groundBottomRight
    }

  def getDroneVizElement(level: Level) =
    BitmapElement(new createjs.Bitmap(image.drone), level.droneElement)

  def getVizElements(level: Level) = level.elements.flatMap {
    case _: DroneElement => None
    case g: GroundElement => {
      Some(BitmapElement(new createjs.Bitmap(groundDirectionToImage(g)), g))
    }
    case f: FireElement => Some(SpriteElement(new createjs.Sprite(fireSpriteSheet, "flames"), f))
  }

  def addElementsToStage(vizElements: Seq[VizElement]): Unit =
    vizElements.foreach {
      case b: BitmapElement => stage.addChild(b.bitmap)
      case s: SpriteElement => stage.addChild(s.sprite)
    }

}