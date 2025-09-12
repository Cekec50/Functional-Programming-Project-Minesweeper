package model

import scala.annotation.tailrec
import scala.swing.{Dimension, GridBagPanel}

class Board(layout: List[List[String]]) extends GridBagPanel {
  val rows: Int = layout.size
  val cols: Int = layout.head.size
  val cellSize: Int = 40

  // === Grid of buttons ===
  val fields: List[List[Field]] =
    List.tabulate(rows, cols) { (r, c) =>
      layout(r)(c) match {
        case "#" => new Field(true)
        case "-" => new Field(false)
        case _ => throw new IllegalArgumentException("Unknown symbol found in file!")
      }
    }

  // Add buttons to grid
  for (r <- 0 until rows; c <- 0 until cols) {
    val constraints = new Constraints
    constraints.gridx = c
    constraints.gridy = r
    add(fields(r)(c), constraints)
  }
  // Make the board size match exactly
  preferredSize = new Dimension(cols * cellSize, rows * cellSize)
  maximumSize   = preferredSize
  minimumSize   = preferredSize

  def adjacent: List[(Int, Int)] = List(
    (-1, -1),(-1, 0),(-1, 1),
    (0, -1),         (0, 1),
    (1, -1), (1, 0), (1, 1)
  )
  def getAdjacentMinesNumber(field: Field): Integer = {
    var number = 0
    for (r <- 0 until rows; c <- 0 until cols if fields(r)(c) == field) {
      for ((i, j) <- adjacent if r + i >= 0 && c + j >= 0 && r + i < rows && c + j < cols) {
        if(fields(r + i)(c + j).getIsMine) number += 1
      }
    }
    number
  }
  def openField(field: Field): Option[Boolean] = {
    if(field.getIsFlagged || !field.enabled) None
    else if(field.revealField) {
      Some(true)
    }
    else{
      val adjMinesNumber = getAdjacentMinesNumber(field)
      if (adjMinesNumber == 0) openAdjacent(field)
      else field.showNumber(adjMinesNumber)
      Some(false)
    }
  }

  def openAdjacent(field: Field): Unit = {
    for (r <- 0 until rows; c <- 0 until cols if fields(r)(c) == field){
      for ((i, j) <- adjacent if r + i >= 0 && c + j >= 0 && r + i < rows && c + j < cols && fields(r + i)(c + j).enabled && !fields(r + i)(c + j).getIsMine){
        openField(fields(r + i)(c + j))
      }
    }
  }
  def revealAllMines = {
    fields.flatten.filter(field => field.getIsMine).foreach(field => field.revealField)
  }

  def flagAllMines = {
    fields.flatten.filter(field => field.getIsMine).foreach(field => field.flagField(true))
  }
}
