package me.michaelgagnon.chopper

object Level {
  val levelMap = Map(
    "chopper1" -> Level1
  )
}

sealed trait Level {
  val dim: Xy
  val numBackgrounds: Int
  val elements: Seq[GameElement]
}

object Level1 extends Level {
  val dim = Xy(100, 200)
  val numBackgrounds = 2
  val elements = Seq[GameElement](
    //WaterElement(250.0, -400),
    FireElement(Xy(50.0,  -FireElement.dim.y - GroundElement.dim.y)),
    FireElement(Xy(100.0, -FireElement.dim.y - GroundElement.dim.y)),
    FireElement(Xy(150.0, -FireElement.dim.y - GroundElement.dim.y))
  )
}