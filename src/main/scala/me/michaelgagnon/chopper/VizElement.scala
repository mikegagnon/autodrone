package me.michaelgagnon.chopper

import com.scalawarrior.scalajs.createjs

// TODO: maybe implicit conversion from bitmap and sprite to BitmapSprite?

// The reason we use T <: GameElement (instead of simply GameElement) is so we can we can do things
// like:
//
//    val droneVizElement: BitmapVizElement[DroneElement] = viz.getDroneVizElement(level)
//    droneVizElement.gameElement.updateState(Xy(thrustX, thrustY))
//
// since droneVizElement.gameElement.updateState is specific to FlyerElement
//
sealed abstract class VizElement[T <: GameElement] {
  val gameElement: T

  // Set the canvas coordinates for this VizElement
  def setXy(xy: Xy): Unit
  def getXy: Xy
  def addToStage(stage: createjs.Stage): Unit
  def removeFromStage(stage: createjs.Stage): Unit
}

case class BitmapVizElement[T <: GameElement](bitmap: createjs.Bitmap, gameElement: T) extends VizElement[T] {
  def setXy(xy: Xy) = {
    bitmap.x = xy.x
    bitmap.y = xy.y
  }
  def getXy = Xy(bitmap.x, bitmap.y)
  def addToStage(stage: createjs.Stage) {
    stage.addChild(bitmap)
  }
  def removeFromStage(stage: createjs.Stage) {
    stage.removeChild(bitmap)
  }
}

case class SpriteVizElement[T <: GameElement](sprite: createjs.Sprite, gameElement: T) extends VizElement[T] {
  def setXy(xy: Xy) = {
    sprite.x = xy.x
    sprite.y = xy.y
  }
  def getXy = Xy(sprite.x, sprite.y)
  def addToStage(stage: createjs.Stage) {
    stage.addChild(sprite)
  }
  def removeFromStage(stage: createjs.Stage) {
    stage.removeChild(sprite)
  }
}
