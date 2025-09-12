package controller


import scala.io.Source
import model.Board
import ui.GamePanel

import java.io.File
import scala.swing.{Component, Dialog, FileChooser, Window}

object LevelLoader {

  /** Loads a level from a text file where '-' = empty, '#' = mine */
  import scala.io.Source
  import java.io.File

  def loadFile(parent: Window): File = {
    val chooser = new FileChooser(new java.io.File("."))
    chooser.title = "Select a Level File or Saved Game"

    if (chooser.showOpenDialog(parent) == FileChooser.Result.Approve) {
      chooser.selectedFile
    } else {
      throw new IllegalStateException("No file selected")
    }
  }

  def loadLevelFromFile(file: File): (Board, Int, Int) = {
    val lines = Source.fromFile(file).getLines().toVector

    if (lines.isEmpty)
      throw new IllegalArgumentException("Level file is empty")

    val rows = lines.length
    val cols = lines.head.length

    // Validate all rows same length
    if (!lines.forall(_.length == cols))
      throw new IllegalArgumentException("Level file is not rectangular")

    // Convert lines into List[List[String]]
    val layout: List[List[String]] = lines.map { line =>
      line.map {
        case '#' => "#"   // mine
        case '-' => "-"   // empty cell
        case other => throw new IllegalArgumentException(
          s"Invalid character '$other' in level file"
        )
      }.toList.map(_.toString)
    }.toList
    println("Layout: " + layout)
    println("Lines: " + lines)
    // Now you can pass this layout into your Board constructor
    (new Board(layout), 0, 0)
  }

  def loadMovesFromFile(parent: Window, board: Board):Unit = {
    val file = loadFile(parent)
    val chooser = new FileChooser(new java.io.File("."))
    chooser.title = "Select a Moves File"

    if (chooser.showOpenDialog(gamePanel) == FileChooser.Result.Approve) {
      val file = chooser.selectedFile
      try {
        println("Loaded moves at " +  file.getAbsolutePath)
        playMovesFromFile(file, board)
      } catch {
        case ex: Exception =>
          Dialog.showMessage(
            parent = this,
            message = s"Failed to load moves: ${ex.getMessage}",
            title = "Error",
            messageType = Dialog.Message.Error
          )
      }
    }
  }





}
