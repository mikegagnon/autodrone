package me.michaelgagnon.chopper

abstract class GameElement(val origPosition: Xy) {
  val currentPosition: Xy = Xy(origPosition.x, origPosition.y)
  val dim: Xy
}

object DroneElement {
  val dim = Xy(46.0, 46.0)
}

// TODO: mass radius?
case class DroneElement(override val origPosition: Xy) extends FlyerElement(origPosition) {
  val mass = 1.0
  val radius = 1.0
  val dim = DroneElement.dim
}

object WaterElement {
  val dim = Xy(50.0, 50.0)
  val interDelay = 500
}

case class WaterElement(override val origPosition: Xy) extends FlyerElement(origPosition) {
  val mass = 1.0
  val radius = 1.0
  val dim = WaterElement.dim
}

sealed trait GroundDirection

object GroundElement {
  val dim = Xy(32.0, 32.0)
  sealed trait EnumVal
  case object TopLeft extends EnumVal
  case object TopCenter extends EnumVal
  case object TopRight extends EnumVal
  case object BottomLeft extends EnumVal
  case object BottomCenter extends EnumVal
  case object BottomRight extends EnumVal
}

case class GroundElement(override val origPosition: Xy, direction: GroundElement.EnumVal) extends GameElement(origPosition) {
  val dim = GroundElement.dim
}

object FireElement {
  val dim = Xy(50.0, 100.0)
}

case class FireElement(override val origPosition: Xy) extends GameElement(origPosition) {
  val dim = FireElement.dim
}
