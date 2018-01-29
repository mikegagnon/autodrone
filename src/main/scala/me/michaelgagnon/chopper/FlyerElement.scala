package me.michaelgagnon.chopper

// TODO: cleanup and refactor

object FlyerElement {
  // ?
  val cd = 0.47

  // kg / m^3
  val rho = 1.22

  // gravity. unit?
  val ag = 9.807

  val maxVelocity = Xy(30.0, 30.0)
}

object FlyResult {
  sealed trait EnumVal
  case object StillFlying extends EnumVal
  case object OutOfBounds extends EnumVal
  case class FireCollision(fireElement: FireElement) extends EnumVal
  case class GroundCollision(velocity: Xy) extends EnumVal
}

abstract class FlyerElement(override val origPosition: Xy) extends GameElement(origPosition) {

  val mass: Double
  val radius: Double

  def a = Math.PI * radius * radius / 10000.0
  val velocity = Xy(0.0, 0.0)
  val F = Xy(0.0, 0.0)

  // Calculate acceleration ( F = ma )
  val acceleration = Xy(F.x / mass, FlyerElement.ag + (F.y / mass))

  def altitude: Double = -currentPosition.y / Level.pixelsPerMeter
  
  // TODO: air drag
  // Returns whether or not there was a collision or out of bounds
  def updateState(thrust: Xy, level: Level): FlyResult.EnumVal = {

    var prev = Xy(currentPosition.x, currentPosition.y)

    F.x = -0.5 * FlyerElement.cd * a * FlyerElement.rho * velocity.x * velocity.x * velocity.x / Math.abs(velocity.x)
    F.y = -0.5 * FlyerElement.cd * a * FlyerElement.rho * velocity.y * velocity.y * velocity.y / Math.abs(velocity.y)
        
    F.x = if (F.x.isNaN) 0.0 else F.x
    F.y = if (F.y.isNaN) 0.0 else F.y

    //println("F.x == " + F.x)

    // Calculate acceleration ( F = ma )
    acceleration.x = (F.x / mass) + thrust.x
    acceleration.y = FlyerElement.ag + (F.y / mass) + thrust.y

    // Integrate to get velocity
    velocity.x = velocity.x + acceleration.x * Viz.frameRate;
    velocity.y = velocity.y + acceleration.y * Viz.frameRate;
        
    // Integrate to get position 100?
    currentPosition.x = currentPosition.x + velocity.x * Viz.frameRate * 100
    currentPosition.y = currentPosition.y + velocity.y * Viz.frameRate * 100

    if (!intersect(level.levelElement)) {
      return FlyResult.OutOfBounds
    }

    level.groundElements.foreach { e =>
  
      // TODO: There is a bug here. Imagine the velocity is very high so the flyer wants to move
      // 100 pixels to right. But there is a groudn element 99 pixels to the right. The incorrect
      // behavior is the flyer stops moving 99 pixels to the left of the ground element (prev.x,
      // prev.y) To fix this we would need interpolation or something.
      if (intersect(e)) {
        val prevVelocity = Xy(velocity.x, velocity.y)
        velocity.y = 0
        velocity.x = 0

        currentPosition.x = prev.x
        currentPosition.y = prev.y
        return FlyResult.GroundCollision(prevVelocity)
      }
    }

    level.fireElements.foreach { e =>
      if (intersect(e)) {
        return FlyResult.FireCollision(e)
      }
    }

    return FlyResult.StillFlying
  }

  // TODO: move to somewhere else?
  def intersect(element: GameElement): Boolean = {
    val f = currentPosition
    val e = element.currentPosition

    f.x < e.x + element.dim.x &&
    f.x + dim.x > e.x &&
    f.y < e.y + element.dim.y &&
    dim.y + f.y > e.y
  }

}
