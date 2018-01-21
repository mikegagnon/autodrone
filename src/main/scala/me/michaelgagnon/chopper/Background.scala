package me.michaelgagnon.chopper

import com.scalawarrior.scalajs.createjs

// x is measured in canvas pixels
case class Background(bitmap: createjs.Bitmap, index: Int, dim: Xy)
