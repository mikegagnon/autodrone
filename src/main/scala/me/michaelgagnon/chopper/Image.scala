package me.michaelgagnon.chopper

import com.scalawarrior.scalajs.createjs
import org.scalajs.dom

class Image(queue: createjs.LoadQueue) {
  val drone = queue.getResult("drone").asInstanceOf[dom.raw.HTMLImageElement]
  val fireSprite = queue.getResult("fireSprites").asInstanceOf[dom.raw.HTMLImageElement]
  val background = queue.getResult("background").asInstanceOf[dom.raw.HTMLImageElement]
  val groundCenterTop = queue.getResult("ground-center-top").asInstanceOf[dom.raw.HTMLImageElement]
  val groundLeftTop = queue.getResult("ground-left-top").asInstanceOf[dom.raw.HTMLImageElement]
  val groundRightTop = queue.getResult("ground-right-top").asInstanceOf[dom.raw.HTMLImageElement]
  val groundCenterMiddle = queue.getResult("ground-center-middle").asInstanceOf[dom.raw.HTMLImageElement]
  val groundLeftMiddle = queue.getResult("ground-left-middle").asInstanceOf[dom.raw.HTMLImageElement]
  val groundRightMiddle = queue.getResult("ground-right-middle").asInstanceOf[dom.raw.HTMLImageElement]
  val waterImage = queue.getResult("water").asInstanceOf[dom.raw.HTMLImageElement]
}
