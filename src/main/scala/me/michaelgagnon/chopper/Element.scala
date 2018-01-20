package me.michaelgagnon.chopper

sealed abstract class Element {
  val x: Double
  val y: Double
  val width: Double
  val height: Double
}