package ui

import scala.swing._
import controller.LevelLoader
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
    val (board, seconds, moves) = LevelLoader.loadLevelFromFile(file)
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
    val file = LevelLoader.loadFile(this)
    startGame(file)
  }
}

