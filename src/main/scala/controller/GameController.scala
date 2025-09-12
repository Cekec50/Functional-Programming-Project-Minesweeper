package controller

import model.Board
import model.Field
import ui.GamePanel

import java.io.File
import scala.io.Source
import scala.swing.{Dialog, Window}

class GameController {

  def playMove(click: String, board: Board, field: Field): Unit = {
    click match {
      case "left-click" =>
        board.openField(field) match {
          case Some(true) => gameOver // opened a mine
          case Some(false) =>
            makeMove
            checkGameWon
          case None => () // flagged or already revealed → do nothing
        }
      case "right-click" =>
        field.flagField() match {
          case Some(true) => makeMove
          case None => ()
        }
    }
  }

  def checkGameWon(parent: Window, board: Board) = {
    val gameWon = board.fields.flatten.forall(field => field.getIsMine || !field.enabled)
    if(gameWon){
      board.flagAllMines
      Dialog.showMessage(parent,"You won!\n Score: ???", "Game over")
    }
  }

  def gameOver(parent: Window,board: Board) = {
    stopTimer
    board.revealAllMines
    // Calculate points
    // Disable all buttons
    Dialog.showMessage(parent , "You lost!", "Game over")
  }

  def playMovesFromFile(file: File, board: Board): Unit = {
    val lines = Source.fromFile(file).getLines()

    for (line <- lines) {
      val move = line.trim
      if (move.nonEmpty) {
        move.head match {
          case 'L' =>
            val (x, y) = parseCoordinates(move)
            val field = board.fields(x)(y)
            board.openField(field) match {
              case Some(true) => gameOver // opened a mine
              case Some(false) =>
                makeMove
                checkGameWon
              case None => () // flagged or already revealed → do nothing
            }

          case 'D' =>
            val (x, y) = parseCoordinates(move)
            val field = board.fields(x)(y)
            field.flagField() match {
              case Some(true) => makeMove
              case None => ()
            }

          case other =>
            throw new IllegalArgumentException(s"Invalid move: $move")
        }
      }
    }
  }

  private def parseCoordinates(move: String): (Int, Int) = {
    // e.g. "L(3,4)" → extract (3,4)
    val pattern = """[LD]\((\d+),(\d+)\)""".r
    move match {
      case pattern(xStr, yStr) =>
        val x = xStr.toInt - 1 // convert to 0-based index
        val y = yStr.toInt - 1
        (x, y)
      case _ =>
        throw new IllegalArgumentException(s"Invalid move format: $move")
    }
  }



}
