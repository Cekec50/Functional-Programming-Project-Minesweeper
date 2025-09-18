package controller

import model.Board
import model.Field
import ui.GamePanel

import java.io.File
import scala.io.Source
import scala.swing.{Dialog, Window}

class GameController(
        gameWon: () => Unit,
        gameLost: () => Unit,
        makeMove: () => Unit
                    ) {

  def playMove(click: String, board: Board, field: Field): Unit = {
    click match {
      case "left-click" =>
        board.openField(field) match {
          case Some(true) => gameLost() // opened a mine
          case Some(false) =>
            makeMove()
            checkGameWon(board)
          case None => () // flagged or already revealed → do nothing
        }
      case "right-click" =>
        field.flagField() match {
          case Some(true) => makeMove()
          case None => ()
        }
    }
  }

  def suggestMove(board: Board): Unit = {
    val unopenedSafe = scala.util.Random.shuffle(board.fields.flatten).find(f => !f.getIsMine && f.enabled)
    unopenedSafe.foreach(f => f.flashField())

  }

  def checkGameWon(board: Board) = {
    val gameWonBool = board.fields.flatten.forall(field => field.getIsMine || !field.enabled)
    if(gameWonBool) gameWon()
  }

  def calculatePoints(seconds: Int, moves: Int): Int = {
    1000 - seconds - moves
  }



  def playMovesFromFile(file: File, board: Board): Unit = {
    val lines = Source.fromFile(file).getLines()

    for (line <- lines) {
      val move = line.trim
      if (move.nonEmpty) {
        move.head match {
          case 'L' =>
            val (x, y) = parseCoordinates(move)
            playMove("left-click", board, board.fields(x)(y))
          case 'D' =>
            val (x, y) = parseCoordinates(move)
            playMove("right-click", board, board.fields(x)(y))
          case _ =>
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
