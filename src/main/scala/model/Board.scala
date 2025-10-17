package model

import scala.swing.{Dimension, GridBagPanel}

class Board(layout: List[List[String]]) extends GridBagPanel {
  val rows: Int = layout.size
  val cols: Int = layout.head.size
  val cellSize: Int = 40
  preferredSize = new Dimension(cols * cellSize, rows * cellSize)
  minimumSize = new Dimension(10 * cellSize, 5 * cellSize)

  // === Grid of buttons ===
  val fields: List[List[Field]] =
    List.tabulate(rows, cols) { (r, c) =>
      layout(r)(c) match {
        case "#" =>   new Field(true)   // MINE FIELD
        case "-" =>   new Field(false)  // NORMAL FIELD
        case "!" =>   new Field(true)   // FLAGGED MINE FIELD
        case "?" =>   new Field(false)  // FLAGGED NORMAL FIELD
        case "+" =>   new Field(false)  // OPENED FIELD
        case _ => throw new IllegalArgumentException("Unknown symbol found in file!")
      }
    }
  for (r <- 0 until rows; c <- 0 until cols  if layout(r)(c) == "!" || layout(r)(c) == "?"){
    fields(r)(c).flagField()
  }
  for (r <- 0 until rows; c <- 0 until cols  if layout(r)(c) == "+"){
    openField(fields(r)(c))
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

  private def adjacent: List[(Int, Int)] = List(
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

  def revealAllMinesLevel = {
    fields.flatten.filter(field => field.getIsMine).foreach(field => field.revealFieldLevel)
  }

  def flagAllMines = {
    fields.flatten.filter(field => field.getIsMine).foreach(field => field.flagField(true))
  }

  def getCoordinatesFromField(field: Field): (Int, Int) = {
    fields.zipWithIndex.flatMap {
      case (row, i) =>
        row.zipWithIndex.collect {
          case (elem, j)
            if (elem == field) =>
            (i, j)
        }
    }.head
  }
  def highlighFields(highlightedFields: List[((Int, Int), Boolean)], field: Field):  List[((Int, Int), Boolean)] = {
    if (highlightedFields.isEmpty){
      //println("Added element " + getCoordinatesFromField(field))
      field.highlightField()
      List((getCoordinatesFromField(field), field.getIsMine))
    }
    else if(highlightedFields.size == 1){
      val (i1, j1) = highlightedFields.head._1
      val (i2, j2) = getCoordinatesFromField(field)
      fields.zipWithIndex.flatMap {
        case (row, i) =>
          row.zipWithIndex.collect {
            case (elem, j)
              if i <= math.max(i1, i2) && j <= math.max(j1, j2) && i >= math.min(i1, i2) && j >= math.min(j1, j2)  =>
              //println("Added element " + getCoordinatesFromField(elem))
              elem.highlightField()
              (getCoordinatesFromField(elem), elem.getIsMine)
          }
      }
    }
    else {
      println("Removed elements")
      highlightedFields.foreach(f => fields(f._1._1)(f._1._2).unhighlightField())
      Nil
    }
  }


  def countMines(): Int = {
    fields.flatten.count(f => f.getIsMine)
  }

  def getLayout(onlyMines: Boolean): List[List[String]] = {
    if(onlyMines) fields.map(_.map(field => if (field.getIsMine) "#" else "-"))
    else layout
  }


}
