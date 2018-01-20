package me.michaelgagnon.chopper

abstract class GameElement(val origPosition: Xy) {
  val currentPosition: Xy = Xy(origPosition.x, origPosition.y)
  val dim: Xy
}

object DroneElement {
  val dim = Xy(46.0, 46.0)
}

case class DroneElement(override val origPosition: Xy) extends GameElement(origPosition) {
  val dim = DroneElement.dim
}

object GroundElement {
  val dim = Xy(32.0, 32.0)
}

case class GroundElement(override val origPosition: Xy) extends GameElement(origPosition) {
  val dim = GroundElement.dim
}

object FireElement {
  val dim = Xy(50.0, 100.0)
}

case class FireElement(override val origPosition: Xy) extends GameElement(origPosition) {
  val dim = FireElement.dim
}
