package me.michaelgagnon.chopper

import org.scalajs.dom
import dom.Element
import org.querki.jquery._
import scala.collection.mutable

object ChopperGlobal {

  val games = mutable.Map[String, Game]()

  def apply() {
    $(".chopper-div").foreach { div: Element =>
      games(div.id) = new Game(div.id)
    }
  }
}