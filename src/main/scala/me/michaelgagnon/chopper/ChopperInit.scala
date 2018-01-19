package me.michaelgagnon.chopper

import org.querki.jquery._

object ChopperInit {
  def apply() {
    $(".chopper-div").foreach { i => ()
      println(i.id)
    }
  }
}