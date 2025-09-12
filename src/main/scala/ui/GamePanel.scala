package ui

import controller.{GameController, LevelLoader}

import scala.swing._
import javax.swing.{Timer => SwingTimer}
import java.awt.event.{ActionEvent, ActionListener}
import scala.swing.event._
import model.Field
import model.Board

import java.io.File
import scala.io.Source

class GamePanel(frame:MainFrameUI, board: Board, startSeconds: Int = 0, startMoves: Int = 0) extends BorderPanel {
  private var seconds = startSeconds
  private var moves = startMoves
  private val gameController: GameController = new GameController()
  private val helpButton = new Button("Help")
  private val loadMovesButton = new Button("Load Moves")
  private val saveButton = new Button("Save")
  private val menuButton = new Button("Menu")

  val timeLabel = new Label("Time: 00:00")
  val movesLabel = new Label(s"Moves: $moves")

  // Timer: fires every 1000 ms (1 sec)
  private val timer = new SwingTimer(1000, (e: ActionEvent) => {
    seconds += 1
    timeLabel.text = f"Time: ${seconds / 60}%02d:${seconds % 60}%02d"
  })

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

  // Start and stop methods
  def startTimer = {
    timeLabel.text = "Time: 00:00"
    timer.start()
  }

  def stopTimer = {
    timer.stop()
  }

  def makeMove= {
    moves += 1
    movesLabel.text = s"Moves: $moves"
  }

  reactions += {
    case ButtonClicked(`helpButton`) => ???)
    case ButtonClicked(`loadMovesButton`) => gameController.playMovesFromFile(LevelLoader.loadFile(frame), board)
    case ButtonClicked(`saveButton`) => ???)
    case ButtonClicked(`menuButton`) => frame.showMenu())
    case ButtonClicked(field: Field) =>
      gameController.playMove("left-click", board, field)
      if(!timer.isRunning) startTimer
    case e: MouseClicked if e.peer.getButton == java.awt.event.MouseEvent.BUTTON3 =>
      val field = e.source.asInstanceOf[Field]
      gameController.playMove("right-click", board, field)
  }





}
