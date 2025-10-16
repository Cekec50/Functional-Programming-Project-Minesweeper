package controller

import model.{Board, Click, Field}

import java.io.File
import scala.io.Source
import scala.swing.Frame

class GameController(
        gameWon: () => Unit,
        gameLost: () => Unit,
        makeMove: () => Unit
                    ) {
  private var gameOver = false

  def getGameState: Boolean = gameOver

  def changeGameState(): Unit = {
    gameOver = true
  }
  private def endGame(onEnd: => Unit): Unit = {
    gameOver = true
    onEnd
  }
  def playMove(click: Click, board: Board, field: Field): Unit = {
    if (!gameOver){
      click match {
        case Click.Left =>
          board.openField(field) match {
            case Some(true) => endGame(gameLost()) // Opened a mine
            case Some(false) => makeMove(); checkGameWon(board)
            case None => () // Flagged or already revealed - Do nothing
          }
        case Click.Right =>
          field.flagField() match {
            case Some(true) => makeMove()
            case None => ()
          }
      }
    }
  }

  def suggestMove(board: Board): Field = {
    val unopenedSafe = scala.util.Random.shuffle(board.fields.flatten).find(f => !f.getIsMine && f.enabled)
    unopenedSafe.foreach(f => f.flashField())
    unopenedSafe.get
  }

  private def checkGameWon(board: Board): Unit = {
    val gameWonBool = board.fields.flatten.forall(field => field.getIsMine || !field.enabled)
    if(gameWonBool) endGame(gameWon())
  }

  def calculatePoints(timeSeconds: Int, movesMade: Int): Int = {
    val K = 10000
    val seconds = Math.max(timeSeconds, 1)
    val moves = Math.max(movesMade, 1)
    val rawPoints = K.toDouble / (seconds * moves)
    Math.max(rawPoints.round.toInt, 1) // at least 1 point
  }

  def playMovesFromFile(frame: Frame, board: Board): Unit = {
    try {
      val file = FileController.loadFile(frame)
      playMovesFromGivenFile(file, board)
    } catch {
      case e: IllegalStateException => println(e.getMessage)
      case e: IllegalArgumentException => println(e.getMessage)
    }
  }
  def playMovesFromGivenFile(file: File,  board: Board): Unit = {

      val lines = Source.fromFile(file).getLines()

      for (line <- lines) {
        val move = line.trim
        if (move.nonEmpty) {
          move.head match {
            case 'L' =>
              val (x, y) = parseCoordinates(move)
              if(x < board.rows && y < board.cols && x >= 0 && y >= 0)
                playMove(Click.Left, board, board.fields(x)(y))
            case 'D' =>
              val (x, y) = parseCoordinates(move)
              if(x < board.rows && y < board.cols && x >= 0 && y >= 0)
                playMove(Click.Right, board, board.fields(x)(y))
            case _ =>
              throw new IllegalArgumentException(s"Invalid move format: $move")
          }
        }
      }
  }

  private def parseCoordinates(move: String): (Int, Int) = {

    val pattern = """[LD]\((\d+),(\d+)\)""".r
    move match {
      case pattern(xStr, yStr) =>
        val x = xStr.toInt - 1
        val y = yStr.toInt - 1
        (x, y)
      case _ =>
        throw new IllegalArgumentException(s"Invalid move format: $move")
    }
  }



}
