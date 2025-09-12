package model

import java.awt.Dimension
import scala.swing.Button

class Field(isMine: Boolean ) extends Button{

  val cellSize: Int = 40
  var fieldText: String = if (isMine) "#" else ""
  var isFlagged: Boolean = false

  preferredSize = new Dimension(cellSize, cellSize)
  minimumSize   = preferredSize
  maximumSize   = preferredSize
  font = new java.awt.Font("Arial", java.awt.Font.BOLD, 16)

  // Remove extra padding
  peer.setMargin(new java.awt.Insets(0, 0, 0, 0))
  focusPainted = false
  contentAreaFilled = true

  def getIsMine = isMine

  def getIsFlagged = isFlagged
  def revealField: Boolean = {
      enabled = false
      text = fieldText
      isMine
  }
  def flagField(bool: Boolean = false): Option[Boolean] = {
    if(enabled){
      isFlagged = if (bool) bool else !isFlagged
      text = if (isFlagged) "F" else ""
      Some(true)
    }
    else None
  }

  def showNumber(number: Int) = {
    text = number.toString
  }

}
