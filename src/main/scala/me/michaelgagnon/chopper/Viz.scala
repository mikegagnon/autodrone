package me.michaelgagnon.chopper

import com.scalawarrior.scalajs.createjs
import org.querki.jquery._
import scala.scalajs.js

object Viz {
  val fps = 30.0
  val frameRate = 1.0 / fps
  val explosionDuration = 800.0

  val manifest = js.Array(
    js.Dictionary("src" -> "img/drone-bw.png", "id" -> "drone"),
    js.Dictionary("src" -> "img/fire-small-sprites.png", "id" -> "fireSprites"),
    js.Dictionary("src" -> "img/explosion.png", "id" -> "explosionSprites"),
    js.Dictionary("src" -> "img/background-dark.png", "id" -> "background"),
    js.Dictionary("src" -> "img/ground-top-center.png", "id" -> "ground-top-center"),
    js.Dictionary("src" -> "img/ground-top-left.png", "id" -> "ground-top-left"),
    js.Dictionary("src" -> "img/ground-top-right.png", "id" -> "ground-top-right"),
    js.Dictionary("src" -> "img/ground-bottom-center.png", "id" -> "ground-bottom-center"),
    js.Dictionary("src" -> "img/ground-bottom-left.png", "id" -> "ground-bottom-left"),
    js.Dictionary("src" -> "img/ground-bottom-right.png", "id" -> "ground-bottom-right"),
    js.Dictionary("src" -> "img/water-small.png", "id" -> "water"),
    js.Dictionary("src" -> "img/youwin.png", "id" -> "youwin")
  )
}

class Viz(val level: Level, val id: String, val image: Image) {

  val div = $(s"#$id")

  val canvasId = id + "canvas"

  val canvas = div.find("canvas")

  // TODO: read canvas id instead of write canvasid
  canvas.attr("id", canvasId)

  val scaleContainer = new createjs.Container()

  val stage = new createjs.Stage(canvasId)

  val canvasSize = Xy(
    canvas.attr("width").get.toDouble,
    canvas.attr("height").get.toDouble)

  val camera = new Camera(canvasSize, level.dim)

  val fireSpriteSheet = new createjs.SpriteSheet(
    js.Dictionary(
      "images" -> js.Array(image.fire),
      "frames" -> js.Dictionary(
        "width" -> FireElement.dim.x,
        "height" -> FireElement.dim.y,
        "regX" -> 0,
        "regY" -> 0
      ),
      "animations" -> js.Dictionary(
        "flames" -> js.Array(0, 1, "flames")
      )
    )
  )

  val explosionSpriteSheet = new createjs.SpriteSheet(
    js.Dictionary(
      "images" -> js.Array(image.explosion),
      "frames" -> js.Dictionary(
        "width" -> ExplosionElement.dim.x,
        "height" -> ExplosionElement.dim.y,
        "regX" -> 0,
        "regY" -> 0
      ),
      "animations" -> js.Dictionary(
        "nothing" -> js.Array(0),
        "explode" -> js.Array(0, 81, "nothing", 5)
      )
    )
  )

  val youWinBitmap = new createjs.Bitmap(image.youwin)
  youWinBitmap.x = 20
  youWinBitmap.y = 20

  // TODO: where to put this code
  val backgrounds = Seq.range(0, level.numBackgrounds).map { i =>
    val bitmap = new createjs.Bitmap(image.background)
    val background = Background(bitmap, i, Xy(image.background.width, image.background.height))
    camera.placeBackground(background)
    stage.addChild(bitmap)
    background
  }

  if (level.drawScale) {
    drawScale()
  }

  var foreground = new createjs.Shape()

  def drawForeground() {
    foreground.graphics.beginFill("#fff")
    foreground.graphics.drawRect(0, 0, canvasSize.x, canvasSize.y)
    foreground.graphics.endFill()
    //if (hide) 
    hideForeground()
    stage.addChild(foreground)
  }

  def hideForeground() {
    foreground.alpha = 0.0
    stage.update()
  }

  def showForeground() {
    foreground.alpha = 0.5
    stage.update()
  }

  def groundDirectionToImage(ground: GroundElement) =
    ground.direction match {
      case GroundElement.TopCenter => image.groundTopCenter
      case GroundElement.TopLeft => image.groundTopLeft
      case GroundElement.TopRight => image.groundTopRight
      case GroundElement.BottomCenter => image.groundBottomCenter
      case GroundElement.BottomLeft => image.groundBottomLeft
      case GroundElement.BottomRight => image.groundBottomRight
    }

  def getDroneVizElement(): BitmapVizElement[DroneElement] = {
    val droneVizElement = BitmapVizElement(new createjs.Bitmap(image.drone), level.droneElement)
    droneVizElement.addToStage(stage)
    camera.setCanvasXy(droneVizElement)
    droneVizElement
  }

  def getFireElements(level: Level) = {
    val fireVizElements = level.fireElements.map { f: FireElement => {
        val s = SpriteVizElement(new createjs.Sprite(fireSpriteSheet, "flames"), f)
        s.sprite.currentFrame = 0
        s.sprite.gotoAndPlay("flames")
        s
      }
    }
    addElementsToStage(fireVizElements)
    fireVizElements
  }

  def newExplosionElement(position: Xy) = {
    val explosionElement = ExplosionElement(position)
    val explosionVizElement = SpriteVizElement(new createjs.Sprite(explosionSpriteSheet, "flames"), explosionElement)
    explosionVizElement.sprite.currentFrame = 0
    explosionVizElement.sprite.gotoAndPlay("explode")
    updateCanvasCoodrinates(explosionVizElement)
    explosionVizElement.addToStage(stage)
    explosionVizElement
  }

  def getGroundElements(level: Level) = {
    val groundVizElements = level.groundElements.map { g: GroundElement => {
        BitmapVizElement(new createjs.Bitmap(groundDirectionToImage(g)), g)
      }
    }
    addElementsToStage(groundVizElements)
    groundVizElements
  }

  def addElementsToStage(vizElements: Seq[VizElement[_ <: GameElement]]): Unit = {
    vizElements.foreach { v : VizElement[_ <: GameElement] =>
      updateCanvasCoodrinates(v)
      v.addToStage(stage)
    }
  }

  def updateCanvasCoodrinates(v: VizElement[_ <: GameElement]) {
    camera.setCanvasXy(v)
  }

  def updateBackground() {
    backgrounds.foreach(camera.placeBackground(_))
  }

  def addToStage(v: VizElement[_ <: GameElement]) {
    camera.setCanvasXy(v)
    v.addToStage(stage)
  }

  def newWaterVizElement(waterElement: WaterElement): VizElement[WaterElement] = {
      val waterVizElement: VizElement[WaterElement] = BitmapVizElement(new createjs.Bitmap(image.water), waterElement)
      addToStage(waterVizElement)
      waterVizElement
  }

  def removeVizElement(vizElement: VizElement[_ <: GameElement]) = {
    vizElement.removeFromStage(stage)
  }

  def update() {
    stage.update()
  }

  def update(
      droneVizElement: VizElement[DroneElement],
      fireVizElements: Seq[VizElement[FireElement]],
      groundVizElements: Seq[VizElement[GroundElement]],
      waterElements: Seq[VizElement[WaterElement]]) {
    camera.positionCamera(droneVizElement)
    updateCanvasCoodrinates(droneVizElement)
    fireVizElements.foreach(updateCanvasCoodrinates(_))
    groundVizElements.foreach(updateCanvasCoodrinates(_))
    waterElements.foreach(updateCanvasCoodrinates(_))
    updateBackground()
    camera.setContainerY(scaleContainer)
    stage.update()
  }

  def youwin() {
    stage.addChild(youWinBitmap)
  }

  def reset() {
    stage.removeChild(youWinBitmap)
  }

  def drawScale() {

    val heightInMeters = Math.floor(level.dim.y / Level.pixelsPerMeter).toInt
    
    for(m <- 1 to heightInMeters) {
      val y = canvasSize.y - m * Level.pixelsPerMeter

      val line = new createjs.Shape()
      line.graphics.setStrokeStyle(1).beginStroke("#000")
      line.graphics.moveTo(0, y)
      line.graphics.lineTo(15, y)
      line.graphics.endStroke()
      scaleContainer.addChild(line)

      var text = new createjs.Text(m.toString + "m", "15px Arial", "#000")
      text.x = 20
      text.y = y + 5
      text.textBaseline = "alphabetic"
      scaleContainer.addChild(text)
    }

    stage.addChild(scaleContainer)

    stage.update()
  }
}