package me.michaelgagnon.chopper

import org.querki.jquery._

class Game(id: String) {

  val div = $(s"#$id")

  val canvasId = id + "canvas"

  val canvas = div.find("canvas")

  canvas.attr("id", canvasId)

  val canvasSize = Xy(
    canvas.attr("width").toOption.get.toDouble,
    canvas.attr("height").toOption.get.toDouble)

}