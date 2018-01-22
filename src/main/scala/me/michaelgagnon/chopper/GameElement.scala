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
  // TODO: rm
  val interDelay = 500
}

case class WaterElement(override val origPosition: Xy) extends FlyerElement(origPosition) {
  val mass = 1.0
  val radius = 1.0
  val dim = WaterElement.dim
}


object ExplosionElement {
  val dim = Xy(100, 100.0)
  val duration = 200.0
}

case class ExplosionElement(override val origPosition: Xy) extends GameElement(origPosition) {
  val dim = ExplosionElement.dim
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
  // For a water to terminate a fire, the water's y coordinate must be within coreHeight
  // from the base of the fire
  val coreHeight = 30.0
}

case class FireElement(override val origPosition: Xy) extends GameElement(origPosition) {
  val dim = FireElement.dim
}
