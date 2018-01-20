package me.michaelgagnon.chopper


class Camera(val canvasSize: Xy) {

  val xMargin = canvasSize.x / 2.0
  val yMargin = canvasSize.y / 2.0
  val leftBorder = xMargin
  val rightBorder = canvasSize.x - xMargin
  val upBorder = yMargin
  val downBorder = canvasSize.y - yMargin

  // TODO: document
  val yMinimum = -500

  // "level" coordinates for the camera (as opposed to canvas coordinates)
  var x: Double = 0.0
  var y: Double = -canvasSize.y

  // Convert level coordinate to canvas coordinate
  def toCanvas(levelCoordinate: Xy) = Xy(levelCoordinate.x - x, levelCoordinate.y - y)

}