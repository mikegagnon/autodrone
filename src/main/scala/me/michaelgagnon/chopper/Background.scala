package me.michaelgagnon.chopper

import com.scalawarrior.scalajs.createjs

object Background {
  val scalingFactor = 0.7
}

// x is measured in canvas pixels
case class Background(bitmap: createjs.Bitmap, index: Int, dim: Xy)
