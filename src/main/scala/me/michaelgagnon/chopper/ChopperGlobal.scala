package me.michaelgagnon.chopper

import org.scalajs.dom
import dom.Element
import org.querki.jquery._
import scala.collection.mutable
import scala.scalajs.js
import com.scalawarrior.scalajs.createjs



object ChopperGlobal {

  val games = mutable.Map[String, Game]()

  val manifest = js.Array(
    js.Dictionary("src" -> "img/drone-bw.png", "id" -> "drone"),
    js.Dictionary("src" -> "img/fire-small-sprites.png", "id" -> "fireSprites"),
    js.Dictionary("src" -> "img/background-dark.png", "id" -> "background"),
    js.Dictionary("src" -> "img/ground-center-top.png", "id" -> "ground-center-top"),
    js.Dictionary("src" -> "img/ground-left-top.png", "id" -> "ground-left-top"),
    js.Dictionary("src" -> "img/ground-right-top.png", "id" -> "ground-right-top"),
    js.Dictionary("src" -> "img/ground-center.png", "id" -> "ground-center-middle"),
    js.Dictionary("src" -> "img/ground-left.png", "id" -> "ground-left-middle"),
    js.Dictionary("src" -> "img/ground-right.png", "id" -> "ground-right-middle"),
    js.Dictionary("src" -> "img/water-small.png", "id" -> "water")
  )

  def apply() {
    $(".chopper-div").foreach { div: Element =>
      games(div.id) = new Game(div.id)
    }

    val queue = new createjs.LoadQueue()
    queue.on("complete", loaded _)
    queue.loadManifest(manifest)
  }

  def loaded(x: Object): Boolean = {

    //Controller.droneGame = Some(new DroneGame(queue))
    println("asd")
    true
  }
}