package me.michaelgagnon.chopper

import com.scalawarrior.scalajs.createjs
import org.querki.jquery._

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

  def loadLevel(level: Level) {
    
  }

}