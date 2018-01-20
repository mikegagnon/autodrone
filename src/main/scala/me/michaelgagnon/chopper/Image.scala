package me.michaelgagnon.chopper

import com.scalawarrior.scalajs.createjs
import org.scalajs.dom

class Image(queue: createjs.LoadQueue) {
  val background = queue.getResult("background").asInstanceOf[dom.raw.HTMLImageElement]
  val drone = queue.getResult("drone").asInstanceOf[dom.raw.HTMLImageElement]
  val fire = queue.getResult("fireSprites").asInstanceOf[dom.raw.HTMLImageElement]
  val groundTopCenter = queue.getResult("ground-center-top").asInstanceOf[dom.raw.HTMLImageElement]
  val groundTopLeft = queue.getResult("ground-left-top").asInstanceOf[dom.raw.HTMLImageElement]
  val groundTopRight = queue.getResult("ground-right-top").asInstanceOf[dom.raw.HTMLImageElement]
  val groundBottomCenter= queue.getResult("ground-center-middle").asInstanceOf[dom.raw.HTMLImageElement]
  val groundBottomLeft = queue.getResult("ground-left-middle").asInstanceOf[dom.raw.HTMLImageElement]
  val groundBottomRight = queue.getResult("ground-right-middle").asInstanceOf[dom.raw.HTMLImageElement]
  val water = queue.getResult("water").asInstanceOf[dom.raw.HTMLImageElement]
}
