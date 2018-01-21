package me.michaelgagnon.chopper

class Camera(val canvasSize: Xy, val levelWidth: Double) {

  // TODO: document
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

  def setCanvasXy(v: VizElement[_ <: GameElement]) {
    val levelCoordinate = v.gameElement.currentPosition
    val canvasCoordinate = Xy(levelCoordinate.x - x, levelCoordinate.y - y)
    v.setXy(canvasCoordinate)
  }

  def positionCamera(droneVizElement: VizElement[DroneElement]) {

    val droneXy = droneVizElement.getXy

    if (droneXy.x > rightBorder) {
      x += droneXy.x - rightBorder
    } else if (droneXy.x < leftBorder) {
      x -= leftBorder - droneXy.x
    }

    x = Math.max(x, 0)
    x = Math.min(x, levelWidth - canvasSize.x)
  }
}
