package me.michaelgagnon.chopper

import org.scalajs.dom
import dom.Element
import org.querki.jquery._
import scala.collection.mutable
import scala.scalajs.js
import com.scalawarrior.scalajs.createjs

object Global {

  val games = mutable.Map[String, Game]()

  var currentGameId = "chopper1"

  // TODO: put in viz?
  val manifest = js.Array(
    js.Dictionary("src" -> "img/drone-bw.png", "id" -> "drone"),
    js.Dictionary("src" -> "img/fire-small-sprites.png", "id" -> "fireSprites"),
    js.Dictionary("src" -> "img/background-dark.png", "id" -> "background"),
    js.Dictionary("src" -> "img/ground-top-center.png", "id" -> "ground-top-center"),
    js.Dictionary("src" -> "img/ground-top-left.png", "id" -> "ground-top-left"),
    js.Dictionary("src" -> "img/ground-top-right.png", "id" -> "ground-top-right"),
    js.Dictionary("src" -> "img/ground-bottom-center.png", "id" -> "ground-bottom-center"),
    js.Dictionary("src" -> "img/ground-bottom-left.png", "id" -> "ground-bottom-left"),
    js.Dictionary("src" -> "img/ground-bottom-right.png", "id" -> "ground-bottom-right"),
    js.Dictionary("src" -> "img/water-small.png", "id" -> "water")
  )

  val queue = new createjs.LoadQueue()

  def apply() {
    queue.on("complete", loaded _)
    queue.loadManifest(manifest)
  }

  def loaded(x: Object): Boolean = {

    val image = new Image(queue)

    $(".chopper-div").foreach { div: Element =>
      val level = Level.levelMap(div.id)
      games(div.id) = new Game(new Viz(div.id, image), level)
    }

    createjs.Ticker.setFPS(Viz.fps)
    createjs.Ticker.addEventListener("tick", tickReceive _)

    true
  }


  def tickReceive(e: js.Dynamic): Boolean = {
    games(currentGameId).tick()
    true
  }
}