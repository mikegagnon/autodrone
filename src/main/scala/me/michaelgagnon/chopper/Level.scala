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
  val fireElements: Seq[FireElement]
  val groundElements: Seq[GroundElement]
}

object Level1 extends Level {
  val dim = Xy(2000, 600)
  val numBackgrounds = 3
  val droneElement = DroneElement(Xy(100.0, -DroneElement.dim.y - GroundElement.dim.y))


  val fireElements = Seq[FireElement](
    FireElement(Xy(300.0, -FireElement.dim.y - GroundElement.dim.y)),
    FireElement(Xy(350.0, -FireElement.dim.y - GroundElement.dim.y)),
    FireElement(Xy(400.0, -FireElement.dim.y - GroundElement.dim.y))
  )

  val groundElements =
  List.range(0, 5).map { i => GroundElement(Xy(i * GroundElement.dim.x, -GroundElement.dim.y), GroundElement.TopCenter) } ++
  List(
    GroundElement(Xy(5 * GroundElement.dim.x, -GroundElement.dim.y), GroundElement.TopRight),
    GroundElement(Xy(9 * GroundElement.dim.x, -GroundElement.dim.y), GroundElement.TopLeft)
    ) ++ 
  List.range(10, 15).map { i => GroundElement(Xy(i * GroundElement.dim.x, -GroundElement.dim.y), GroundElement.TopCenter) } ++
  List(
    GroundElement(Xy(15 * GroundElement.dim.x, -GroundElement.dim.y), GroundElement.TopRight),
    GroundElement(Xy(20 * GroundElement.dim.x, -GroundElement.dim.y), GroundElement.BottomLeft),
    GroundElement(Xy(20 * GroundElement.dim.x, -GroundElement.dim.y * 2), GroundElement.BottomLeft),
    GroundElement(Xy(20 * GroundElement.dim.x, -GroundElement.dim.y * 3), GroundElement.BottomLeft),
    GroundElement(Xy(20 * GroundElement.dim.x, -GroundElement.dim.y * 4), GroundElement.BottomLeft),
    GroundElement(Xy(20 * GroundElement.dim.x, -GroundElement.dim.y * 5), GroundElement.BottomLeft),
    GroundElement(Xy(20 * GroundElement.dim.x, -GroundElement.dim.y * 6), GroundElement.TopLeft),

    GroundElement(Xy(21 * GroundElement.dim.x, -GroundElement.dim.y), GroundElement.BottomCenter),
    GroundElement(Xy(21 * GroundElement.dim.x, -GroundElement.dim.y * 2), GroundElement.BottomCenter),
    GroundElement(Xy(21 * GroundElement.dim.x, -GroundElement.dim.y * 3), GroundElement.BottomCenter),
    GroundElement(Xy(21 * GroundElement.dim.x, -GroundElement.dim.y * 4), GroundElement.BottomCenter),
    GroundElement(Xy(21 * GroundElement.dim.x, -GroundElement.dim.y * 5), GroundElement.BottomCenter),
    GroundElement(Xy(21 * GroundElement.dim.x, -GroundElement.dim.y * 6), GroundElement.TopCenter),
    
    GroundElement(Xy(22 * GroundElement.dim.x, -GroundElement.dim.y), GroundElement.BottomCenter),
    GroundElement(Xy(22 * GroundElement.dim.x, -GroundElement.dim.y * 2), GroundElement.BottomCenter),
    GroundElement(Xy(22 * GroundElement.dim.x, -GroundElement.dim.y * 3), GroundElement.BottomCenter),
    GroundElement(Xy(22 * GroundElement.dim.x, -GroundElement.dim.y * 4), GroundElement.BottomCenter),
    GroundElement(Xy(22 * GroundElement.dim.x, -GroundElement.dim.y * 5), GroundElement.BottomCenter),
    GroundElement(Xy(22 * GroundElement.dim.x, -GroundElement.dim.y * 6), GroundElement.TopCenter),

    GroundElement(Xy(23 * GroundElement.dim.x, -GroundElement.dim.y), GroundElement.BottomRight),
    GroundElement(Xy(23 * GroundElement.dim.x, -GroundElement.dim.y * 2), GroundElement.BottomRight),
    GroundElement(Xy(23 * GroundElement.dim.x, -GroundElement.dim.y * 3), GroundElement.BottomRight),
    GroundElement(Xy(23 * GroundElement.dim.x, -GroundElement.dim.y * 4), GroundElement.BottomRight),
    GroundElement(Xy(23 * GroundElement.dim.x, -GroundElement.dim.y * 5), GroundElement.BottomRight),
    GroundElement(Xy(23 * GroundElement.dim.x, -GroundElement.dim.y * 6), GroundElement.TopRight)
  )
}