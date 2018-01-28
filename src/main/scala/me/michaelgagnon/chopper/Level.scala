package me.michaelgagnon.chopper

object Level {
  val levelMap = Map(
    "chopper1" -> new Level1,
    "chopper2" -> new Level1,
    "chopper3" -> new Level1
  )
  val margin = 100.0

  val pixelsPerMeter = 30.0
}

object GroundMaker {

  // rows are negative indexed just like LevelElement objects
  def rect(rowTop: Int, colLeft: Int, rowBottom: Int, colRight: Int) =

    for {
      r <- rowTop to rowBottom
      c <- colLeft to colRight
    } yield {

      println(r,c)

      val xy = Xy(c * GroundElement.dim.x, r * GroundElement.dim.y)
      val direction =
        if (r == rowTop) {
          if (c == colLeft) {
            GroundElement.TopLeft
          } else if (c == colRight) {
            GroundElement.TopRight
          } else {
            GroundElement.TopCenter
          }
        } else {
          if (c == colLeft) {
            GroundElement.BottomLeft
          } else if (c == colRight) {
            GroundElement.BottomRight
          } else {
            GroundElement.BottomCenter
          }
        }
      GroundElement(xy, direction)
    }

}

case class LevelElement(override val origPosition: Xy, dim: Xy) extends GameElement(origPosition)

sealed trait Level {
  val drawScale: Boolean
  val dim: Xy
  def levelElement = LevelElement(
    Xy(-Level.margin, -dim.y - Level.margin),
    Xy(dim.x + 2 * Level.margin, dim.y + 2 * Level.margin))
  val numBackgrounds: Int
  val droneElement: DroneElement
  val fireElements: Seq[FireElement]
  val groundElements: Seq[GroundElement]
}

class Level1 extends Level {

  //GroundMaker.rect(-5, 0, 0, 10)

  val drawScale = true
  val dim = Xy(2000, 3000)
  val numBackgrounds = 3
  val droneElement = DroneElement(Xy(100.0, -DroneElement.dim.y - GroundElement.dim.y))

  val fireElements = Seq[FireElement](
    FireElement(Xy(300.0, -FireElement.dim.y - GroundElement.dim.y)),
    FireElement(Xy(350.0, -FireElement.dim.y - GroundElement.dim.y)),
    FireElement(Xy(400.0, -FireElement.dim.y - GroundElement.dim.y))
  )

  val groundElements = /*GroundMaker.rect(-1, 0, 0, 10) ++ GroundMaker.rect(-10, 6, 0, 7) */
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
