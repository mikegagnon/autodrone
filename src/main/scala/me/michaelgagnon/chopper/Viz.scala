package me.michaelgagnon.chopper

import com.scalawarrior.scalajs.createjs
import org.querki.jquery._

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

}