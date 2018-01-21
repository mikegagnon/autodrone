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

  def currentGame = games(currentGameId)

  val queue = new createjs.LoadQueue()

  def apply() {
    Controller.init()
    queue.on("complete", loaded _)
    queue.loadManifest(Viz.manifest)
  }

  def loaded(x: Object): Boolean = {

    val image = new Image(queue)

    $(".chopper-div").foreach { div: Element =>
      val gameId = div.id
      val level = Level.levelMap(gameId)
      games(gameId) = new Game(new Viz(gameId, image, level.dim.x), level)
    }

    currentGame.controller.paused = false

    createjs.Ticker.setFPS(Viz.fps)
    createjs.Ticker.addEventListener("tick", tickReceive _)

    true
  }


  def tickReceive(e: js.Dynamic): Boolean = {
    games(currentGameId).tick()
    true
  }
}