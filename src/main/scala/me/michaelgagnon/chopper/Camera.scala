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

/*
  def placeVisual(vle: VisualLevelElement) =
    vle.setXy(XyConst(vle.levelElement.x - x, vle.levelElement.y - y))

  def placeBitmapBackground(ble: BitmapLevelElement) {
    ble.bitmap.x = ble.levelElement.x - x * 0.2
    // TODO: undo 390 const
    ble.bitmap.y = ble.levelElement.y - y * 0.2 + 390
  }

  def update(droneBitmap: XyConst) {
    if (droneBitmap.x > Camera.rightBorder) {
      x += droneBitmap.x - Camera.rightBorder
    } else if (droneBitmap.x < Camera.leftBorder) {
      x -= Camera.leftBorder - droneBitmap.x
    }

    x = Math.max(x, 0)
    x = Math.min(x, Level.width - Config.canvasWidth)
  }
*/
}