package ui

import controller.{GameController, FileController}

import scala.swing._
import javax.swing.{Timer => SwingTimer}
import java.awt.event.ActionEvent
import scala.swing.event._
import model.Field
import model.Board

class GamePanel(frame:MainFrameUI, board: Board, startSeconds: Int = 0, startMoves: Int = 0) extends BorderPanel {
  private var seconds = startSeconds
  private var moves = startMoves
  private val gameController: GameController = new GameController(
    gameWon = () => gameWon(),
    gameLost = () => gameLost(),
    makeMove = () => makeMove()
  )

  private val timeLabel = new Label(f"Time: ${seconds / 60}%02d:${seconds % 60}%02d")
  private val movesLabel = new Label(s"Moves: $moves")
  private val timer = new SwingTimer(1000, (e: ActionEvent) => {
    seconds += 1
    timeLabel.text = f"Time: ${seconds / 60}%02d:${seconds % 60}%02d"
  })

  private val helpButton = new Button("Help")
  private val loadMovesButton = new Button("Load Moves")
  private val saveButton = new Button("Save")
  private val menuButton = new Button("Menu")

  // === Top bar (score, etc.) ===
  private val topBar = new BoxPanel(Orientation.Horizontal) {
    contents += timeLabel
    contents += Swing.HGlue
    contents += movesLabel
    contents += Swing.HGlue

    contents += helpButton
    contents += loadMovesButton
    contents += saveButton
    contents += menuButton
  }

  // === Layout ===
  layout(topBar) = BorderPanel.Position.North
  layout(board)  = BorderPanel.Position.Center

  // === Events ===
  listenTo(helpButton,loadMovesButton,saveButton,menuButton)
  listenTo(board.fields.flatten: _*)
  listenTo(board.fields.flatten.map(_.mouse.clicks): _*)
  //deafTo(board.fields.flatten: _*)


  // Start and stop methods
  def startTimer = {
    timeLabel.text = f"Time: ${seconds / 60}%02d:${seconds % 60}%02d"
    timer.start()
  }

  def stopTimer() = {
    timer.stop()
  }

  def makeMove(numberOfMoves: Int = 1)= {
    moves += numberOfMoves
    movesLabel.text = s"Moves: $moves"
  }

  def disableBoard = {
    deafTo(board.fields.flatten: _*)
    deafTo(board.fields.flatten.map(_.mouse.clicks): _*)
  }
  def gameLost() = {
    stopTimer()
    board.revealAllMines
    disableBoard
    Dialog.showMessage(this , "You lost!", "Game over")
  }

  def gameWon(): Unit = {
    stopTimer()
    board.flagAllMines
    disableBoard
    val points = gameController.calculatePoints(seconds, moves)
    val name = Dialog.showInput(
      this,
      message = s"You won!\nScore: $points\nEnter your name:",
      initial = "Player",
      title = "Victory!"
    )
    name.foreach { n =>
      FileController.saveScore(n, points)
    }

    Dialog.showMessage(this, s"Score saved for $name", "Victory")
  }

  reactions += {
    case ButtonClicked(`helpButton`) => gameController.suggestMove(board); makeMove(20)
    case ButtonClicked(`loadMovesButton`) => gameController.playMovesFromFile(FileController.loadFile(frame), board)
    case ButtonClicked(`saveButton`) => FileController.saveGame(frame, board, moves, seconds)
    case ButtonClicked(`menuButton`) => frame.showMenu()
    case ButtonClicked(field: Field) =>
      gameController.playMove("left-click", board, field)
      if(!timer.isRunning) startTimer
    case e: MouseClicked if e.peer.getButton == java.awt.event.MouseEvent.BUTTON3 =>
      val field = e.source.asInstanceOf[Field]
      gameController.playMove("right-click", board, field)
  }




}
