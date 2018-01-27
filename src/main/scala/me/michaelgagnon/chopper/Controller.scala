package me.michaelgagnon.chopper

import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.html.Button
import scala.collection.mutable
import scala.scalajs.js.annotation._
import org.querki.jquery._

// TODO:
import org.denigma.codemirror.{CodeMirror, EditorConfiguration, Editor}
import org.scalajs.dom
import org.scalajs.dom.raw.HTMLTextAreaElement

import scala.scalajs.js

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
 
  val editorTheme = "eclipse"
  val editorThemeDim = "eclipse-dim"

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

  def initEditor(gameId: String): Editor = {
    val params = js.Dynamic.literal(
      mode = "javascript",
      theme = editorTheme,
      lineNumbers = true
    ).asInstanceOf[EditorConfiguration]

    val text =
"""if (altitude < 6 meters) {
  thrustUp = 10 meters/second^2
} else if (speedUp < 0 meters/second) {
  thrustUp = 9.81 meters/second^2
} else {
  thrustUp = 9.0 meters/second^2
}
"""

    dom.document.getElementById(gameId + "-editor") match {
      case el:HTMLTextAreaElement => {
        val m = CodeMirror.fromTextArea(el,params)
        m.getDoc().setValue(text)
        m
      }
      case _=> throw new IllegalArgumentException("cannot find text area for the code")
    }

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

  def displayError(message: String) {
    $(s"#$gameId-error-box").text(message)
  }

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
      assert(Global.currentEditor.nonEmpty)
      Global.currentEditor.get.setOption("readOnly","nocursor")
      Global.currentEditor.get.setOption("theme", Controller.editorThemeDim)
      Global.currentGame.get.viz.hideForeground()
    } else {
      assert(Global.currentGame.nonEmpty)
      assert(Global.currentEditor.nonEmpty)
      Global.currentGame.get.viz.showForeground()
      Global.currentEditor.get.setOption("readOnly", false)
      Global.currentEditor.get.setOption("theme", Controller.editorTheme)
      Global.currentGameId = None
      playPauseButton.textContent = "Play"
      paused = true
    }

  } 
}
