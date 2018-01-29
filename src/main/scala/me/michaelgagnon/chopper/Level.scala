package me.michaelgagnon.chopper

object Level {

  val programText1 = "\n\n"
  val programText2 = "thrustUp = 15 meters/second^2\n\n"
  val programText3 = "thrustUp = 7.5 meters/second^2\n\n"
  val programText4 = """if (altitude < 6 meters) then {
  thrustUp = 15 meters/second^2 
} else then {
  thrustUp = 0 meters/second^2
}

"""
  val programText5 = """if (altitude < 6 meters) then {
  thrustUp = 15 meters/second^2 
}

"""
  val programText6 = """if (altitude < 9 meters) then {
  thrustUp = 10 meters/second^2 
} else then {
  
  if (speedUp < 0 meters/second) {
    thrustUp = 9.81 meters/second^2
  } else {
    thrustUp = 9 meters/second^2
  }
}

"""
  val programText7 = """if (altitude < 9 meters) then {
  thrustUp = 10 meters/second^2 
} else then {
  
  if (speedUp < 0 meters/second) {
    thrustUp = 9.81 meters/second^2
  } else {
    thrustUp = 9 meters/second^2
  }

  if (speedRight < 1 meters/second) {
    thrustRight = 1.0 meters/second^2
  } else {
    thrustRight = 0.0 meters/second^2
  }
}

"""
  val programText8 = """if (altitude < 9 meters) then {
  thrustUp = 10 meters/second^2 
} else then {
  
  dropWater = true

  if (speedUp < 0 meters/second) {
    thrustUp = 9.81 meters/second^2
  } else {
    thrustUp = 9 meters/second^2
  }

  if (speedRight < 1 meters/second) {
    thrustRight = 1.0 meters/second^2
  } else {
    thrustRight = 0.0 meters/second^2
  }
}

"""

val programText9 = """if (fire == true) {
  if (altitude < 9 meters) then {
    thrustUp = 10 meters/second^2 
  } else then {
    
    dropWater = true

    if (speedUp < 0 meters/second) {
      thrustUp = 9.81 meters/second^2
    } else {
      thrustUp = 9 meters/second^2
    }

    if (speedRight < 0.5 meters/second) {
      thrustRight = 1.0 meters/second^2
    } else {
      thrustRight = 0.0 meters/second^2
    }

    if (fire == false) {

    }
  }
} else {
  dropWater = false
  thrustUp = 9.8 meters/second^2
}

"""
  val levelMap = Map(
    "chopper1" -> new Level1(programText9),
    "chopper2" -> new Level1(programText2),
    "chopper3" -> new Level1(programText3),
    "chopper4" -> new Level1(programText4),
    "chopper5" -> new Level1(programText5),
    "chopper6" -> new Level1(programText6),
    "chopper7" -> new Level1(programText7),
    "chopper8" -> new Level1(programText8),
    "chopper9" -> new Level1(programText9)
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
  val programText: String
}

class Level1(val programText: String) extends Level {

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
