package model

sealed trait Click
object Click {
  case object Left extends Click
  case object Right extends Click
}