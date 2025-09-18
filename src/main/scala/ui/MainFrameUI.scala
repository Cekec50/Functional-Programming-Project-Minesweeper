package ui

import scala.swing._
import controller.FileController
import model.Board

import java.io.File

class MainFrameUI extends MainFrame {
  // Panels
  private val menuPanel: MenuPanel = new MenuPanel(this)
  private val difficultyChooserPanel : DifficultyChooserPanel = new DifficultyChooserPanel(this)
  private var gamePanel: GamePanel = _


  font = new Font("Arial", java.awt.Font.BOLD, 40)
  title = "Minesweeper"
  preferredSize = new Dimension(500, 500)
  contents = menuPanel


  def chooseDifficulty(): Unit = {
    contents = difficultyChooserPanel
    validate()
    repaint()
  }
  def startGame(file: File): Unit = {
    val (board, seconds, moves) = FileController.loadLevelFromFile(file)
    gamePanel = new GamePanel(this, board, seconds, moves)
    contents = gamePanel
    pack() // adjust frame size to fit new preferredSize
  }



  def showMenu(): Unit = {
    contents = menuPanel
    validate()
    repaint()
  }

  def loadLevel():Unit = {
    val file = FileController.loadFile(this)
    startGame(file)
  }

  def openHighscores(): Unit = {
    contents = new HighscorePanel(this)
  }

  def createLevel(): Unit = {
    contents = new LevelCreatorPanel(this, new Board(List.fill(10, 10)("-")))
  }


}

