package me.michaelgagnon.chopper

import com.scalawarrior.scalajs.createjs
import org.scalajs.dom

class Image(queue: createjs.LoadQueue) {
  val background = queue.getResult("background").asInstanceOf[dom.raw.HTMLImageElement]
  val drone = queue.getResult("drone").asInstanceOf[dom.raw.HTMLImageElement]
  val fire = queue.getResult("fireSprites").asInstanceOf[dom.raw.HTMLImageElement]
  val explosion = queue.getResult("explosionSprites").asInstanceOf[dom.raw.HTMLImageElement]
  val groundTopCenter = queue.getResult("ground-top-center").asInstanceOf[dom.raw.HTMLImageElement]
  val groundTopLeft = queue.getResult("ground-top-left").asInstanceOf[dom.raw.HTMLImageElement]
  val groundTopRight = queue.getResult("ground-top-right").asInstanceOf[dom.raw.HTMLImageElement]
  val groundBottomCenter= queue.getResult("ground-bottom-center").asInstanceOf[dom.raw.HTMLImageElement]
  val groundBottomLeft = queue.getResult("ground-bottom-left").asInstanceOf[dom.raw.HTMLImageElement]
  val groundBottomRight = queue.getResult("ground-bottom-right").asInstanceOf[dom.raw.HTMLImageElement]
  val water = queue.getResult("water").asInstanceOf[dom.raw.HTMLImageElement]
}
