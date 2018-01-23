package me.michaelgagnon.chopper

import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.html.Button
import scala.collection.mutable
import scala.scalajs.js.annotation._

// TODO: cleanup and refactor

object KeyCode {
  val Up = 38
  val Down = 40
  val Left = 37
  val Right = 39
  val Space = 32
  val arrowKeys = Set(Up, Down, Left, Right)
}

@JSExportTopLevel("Controller")
object Controller {
 
  val keyPressed = mutable.Map[Int, Boolean](
    KeyCode.Up -> false,
    KeyCode.Down -> false,
    KeyCode.Left -> false,
    KeyCode.Right -> false,
    KeyCode.Space -> false)

  def init() {
    dom.window.onkeydown = Controller.onkeydown _
    dom.window.onkeyup = Controller.onkeyup _
  }

  def onkeydown(e: dom.KeyboardEvent) {
    Global.currentGame.foreach { game =>
      val key = e.keyCode.toInt
      if (KeyCode.arrowKeys.contains(key) || key == KeyCode.Space) {
        if (!game.controller.paused) e.preventDefault()
        keyPressed(key) = true
      }
    }
  }

  def onkeyup(e: dom.KeyboardEvent) {
    Global.currentGame.foreach { game =>
      val key = e.keyCode.toInt
      if (KeyCode.arrowKeys.contains(key) || key == KeyCode.Space) {
        if (!game.controller.paused) e.preventDefault()
        keyPressed(key) = false
      }
    }
  }

  @JSExport("playPauseClick")
  def playPauseClick(gameId: String): Unit = {
    Global.games(gameId).controller.playPauseClick()
  }

}

class Controller(val gameId: String) {

  var paused = true

  // TODO: use jquery
  def getButton(buttonName: String): Button = {
    val id = s"#$gameId-$buttonName"

    val queryResult = document.querySelector(id)
    queryResult match {
      case button: Button => button
      // TODO: not the right exception
      case _ => throw new IllegalArgumentException("Not a button")
    }
  }

  val playPauseButton: Button = getButton("playPauseButton")

  def playPauseClick(): Unit = {

    Global.currentGame.foreach { game =>
      assert(!game.controller.paused)

      // Pause the current game (but not if the current game is this game)
      if (game.gameId != gameId) {
        game.controller.playPauseClick()
      }

    }

    if (paused) {
      assert(Global.currentGame.isEmpty)
      Global.currentGameId = Some(gameId)
      playPauseButton.textContent = "Pause"
      paused = false
    } else {
      assert(Global.currentGame.nonEmpty)
      Global.currentGameId = None
      playPauseButton.textContent = "Play"
      paused = true
    }

  } 
}
