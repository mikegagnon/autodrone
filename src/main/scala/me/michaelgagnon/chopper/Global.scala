package me.michaelgagnon.chopper

import org.scalajs.dom
import dom.Element
import org.querki.jquery._
import scala.collection.mutable
import scala.scalajs.js
import com.scalawarrior.scalajs.createjs

// TODO:
import org.denigma.codemirror


object Global {

  val games = mutable.Map[String, Game]()

  // TODO: unify games editors into single class or something?
  val editors = mutable.Map[String, codemirror.Editor]()

  var currentGameId: Option[String] = Some("chopper1")

  def currentGame: Option[Game] = currentGameId.map(games(_))

  def currentEditor: Option[codemirror.Editor] = currentGameId.map(editors(_))

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
      games(gameId) = new Game(level, gameId, image)
      editors(gameId) = Controller.initEditor(gameId)
    }

    currentGame.foreach(_.controller.paused = false)
    currentEditor.foreach { e =>
      e.setOption("readOnly","nocursor")
      e.setOption("theme", Controller.editorThemeDim)
    }

    createjs.Ticker.setFPS(Viz.fps)
    createjs.Ticker.addEventListener("tick", tickReceive _)

    true
  }


  def tickReceive(e: js.Dynamic): Boolean = {
    currentGame.foreach(_.tick())
    true
  }
}