package me.michaelgagnon.chopper

// TODO: cleanup and refactor

object FlyerElement {
  val outofBoundsY = 200

  // ?
  val cd = 0.47

  // kg / m^3
  val rho = 1.22

  // gravity. unit?
  val ag = 9.81

  val maxVelocity = Xy(30.0, 30.0)
}

abstract class FlyerElement(override val origPosition: Xy) extends GameElement(origPosition) {

  val mass: Double
  val radius: Double

  def a = Math.PI * radius * radius / 10000.0
  var inBounds = true
  var currentlyFlying = false
  val velocity = Xy(0.0, 0.0)
  val F = Xy(0.0, 0.0)

  // Calculate acceleration ( F = ma )
  val acceleration = Xy(F.x / mass, FlyerElement.ag + (F.y / mass))
  
  // TODO: air drag
  def updateState(thrust: Xy, elements: Seq[GameElement]): Unit = {
    var prev = Xy(currentPosition.x, currentPosition.y)

    F.x = -0.5 * FlyerElement.cd * a * FlyerElement.rho * velocity.x * velocity.x * velocity.x / Math.abs(velocity.x)
    F.y = -0.5 * FlyerElement.cd * a * FlyerElement.rho * velocity.y * velocity.y * velocity.y / Math.abs(velocity.y)
        
    F.x = if (F.x.isNaN) 0.0 else F.x
    F.y = if (F.y.isNaN) 0.0 else F.y

    // Calculate acceleration ( F = ma )
    acceleration.x = (F.x / mass) + thrust.x
    acceleration.y = FlyerElement.ag + (F.y / mass) + thrust.y

    // Integrate to get velocity
    velocity.x = velocity.x + acceleration.x * Viz.frameRate;
    velocity.y = velocity.y + acceleration.y * Viz.frameRate;
        
    // Integrate to get position 100?
    currentPosition.x = currentPosition.x + velocity.x * Viz.frameRate * 100
    currentPosition.y = currentPosition.y + velocity.y * Viz.frameRate * 100

    elements.filter {
      case g: GroundElement => true
      case _ => false
    }.foreach { e=>
  
      if (intersect(e)) {
        velocity.y = 0
        velocity.x = 0

        currentPosition.x = prev.x
        currentPosition.y = prev.y
      }
    }

    if (currentPosition.y > FlyerElement.outofBoundsY) {
      currentlyFlying = false
      inBounds = false
    }


  }

  def intersect(element: GameElement): Boolean = {
    val f = currentPosition
    val e = element.currentPosition

    f.x < e.x + element.dim.x &&
    f.x + dim.x > e.x &&
    f.y < e.y + element.dim.y &&
    dim.y + f.y > e.y
  }

}
