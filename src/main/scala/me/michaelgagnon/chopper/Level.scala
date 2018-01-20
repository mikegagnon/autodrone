package me.michaelgagnon.chopper

object Level {
  val levelMap = Map(
    "chopper1" -> Level1,
    "chopper2" -> Level1
  )
}

sealed trait Level {
  val dim: Xy
  val numBackgrounds: Int
  val droneElement: DroneElement
  val elements: Seq[GameElement]
}

object Level1 extends Level {
  val dim = Xy(100, 200)
  val numBackgrounds = 2
  val droneElement = DroneElement(Xy(100.0, -DroneElement.dim.y - GroundElement.dim.y))
  val elements = Seq[GameElement](
    FireElement(Xy(200.0, -FireElement.dim.y - GroundElement.dim.y)),
    FireElement(Xy(250.0, -FireElement.dim.y - GroundElement.dim.y)),
    FireElement(Xy(300.0, -FireElement.dim.y - GroundElement.dim.y)),
    GroundElement(Xy(200.0, -GroundElement.dim.y), GroundElement.BottomRight)
  )
}