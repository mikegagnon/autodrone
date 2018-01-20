package me.michaelgagnon.chopper

import org.querki.jquery._

class Game(val id: String, val image: Image) {

  val div = $(s"#$id")

  val canvasId = id + "canvas"

  val canvas = div.find("canvas")

  canvas.attr("id", canvasId)

  val canvasSize = Xy(
    canvas.attr("width").get.toDouble,
    canvas.attr("height").get.toDouble)

}