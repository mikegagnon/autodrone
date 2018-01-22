package me.michaelgagnon.chopper

import org.scalajs.dom
import scala.collection.mutable

// TODO: cleanup and refactor

object KeyCode {
  val Up = 38
  val Down = 40
  val Left = 37
  val Right = 39
  val Space = 32
  val arrowKeys = Set(Up, Down, Left, Right)
}

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
    val game = Global.currentGame
    val key = e.keyCode.toInt
    if (KeyCode.arrowKeys.contains(key) || key == KeyCode.Space) {
      if (!game.controller.paused) e.preventDefault()
      keyPressed(key) = true
    }
  }

  def onkeyup(e: dom.KeyboardEvent) {
    val game = Global.currentGame
    val key = e.keyCode.toInt
    if (KeyCode.arrowKeys.contains(key) || key == KeyCode.Space) {
      if (!game.controller.paused) e.preventDefault()
      keyPressed(key) = false
    }
  }

}

class Controller() {
  var paused = true
}
