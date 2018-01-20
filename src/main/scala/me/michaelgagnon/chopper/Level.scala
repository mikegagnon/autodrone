package me.michaelgagnon.chopper

sealed trait Level {
  val dim: Xy
  val numBackgrounds: Int
  val elements: Seq[Element]
}

class Level1 extends Level {
  val dim = Xy(100, 200)
  val numBackgrounds = 2
  val elements = Seq[Element]()
}