package me.michaelgagnon.chopper

import com.scalawarrior.scalajs.createjs

// The reason we use T <: GameElement (instead of simply GameElement) is so we can we can do things
// like:
//
//    val droneVizElement: BitmapVizElement[DroneElement] = viz.getDroneVizElement(level)
//    droneVizElement.gameElement.updateState(Xy(thrustX, thrustY))
//
// since droneVizElement.gameElement is specific to FlyerElement
//
sealed abstract class VizElement[T <: GameElement] {
  val gameElement: T

  // Set the canvas coordinates for this VizElement
  def setXy(xy: Xy): Unit
  def addToStage(stage: createjs.Stage): Unit
}

case class BitmapVizElement[T <: GameElement](bitmap: createjs.Bitmap, gameElement: T) extends VizElement[T] {
  def setXy(xy: Xy) = {
    bitmap.x = xy.x
    bitmap.y = xy.y
  }
  def addToStage(stage: createjs.Stage) {
    stage.addChild(bitmap)
  }
}

case class SpriteVizElement[T <: GameElement](sprite: createjs.Sprite, gameElement: T) extends VizElement[T] {
  def setXy(xy: Xy) = {
    sprite.x = xy.x
    sprite.y = xy.y
  }
  def addToStage(stage: createjs.Stage) {
    stage.addChild(sprite)
  }
}
