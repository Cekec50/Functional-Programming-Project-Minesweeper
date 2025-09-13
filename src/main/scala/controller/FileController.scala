package controller


import scala.io.Source
import model.Board
import ui.GamePanel

import java.io.{File, PrintWriter}
import scala.swing.{Component, Dialog, FileChooser, Window}

object LevelLoader {

  /** Loads a level from a text file where '-' = empty, '#' = mine */
  import scala.io.Source
  import java.io.File

  def saveGame(parent: Window, board: Board, moves: Int, seconds: Int): Unit = {
    val chooser = new FileChooser(new java.io.File("."))
    chooser.title = "Save Game"
    if (chooser.showSaveDialog(parent) == FileChooser.Result.Approve) {
      val file = chooser.selectedFile
      val writer = new PrintWriter(file)
      try {
        // Write the board
        for (row <- board.fields) {
          val line = row.map { field =>
            (field.enabled, field.getIsMine, field.isFlagged) match {
              case (true, true, true)  => '!' // mine and flagged
              case (true, true, false) => '#' // mine
              case (true, false, true) => '?' // normal field flagged
              case (true, false, false)=> '-' // normal field
              case (false, _, _) => '+'       // clicked field
            }
          }.mkString
          writer.println(line)
        }
        // Write moves and seconds
        writer.println(s"m:$moves")
        writer.println(s"s:$seconds")
        Dialog.showMessage(parent, "Game saved successfully!", "Save")
      } finally {
        writer.close()
      }
    }
  }
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
    val allLines = Source.fromFile(file).getLines().toVector
    if (allLines.isEmpty)
      throw new IllegalArgumentException("Level file is empty")

    var moves = 0
    var seconds = 0
    var lines = allLines

    // Check if last two lines contain metadata
    if (allLines.length >= 2 &&
      allLines(allLines.length - 2).startsWith("m:") &&
      allLines(allLines.length - 1).startsWith("s:")) {

      moves = allLines(allLines.length - 2).stripPrefix("m:").toInt
      seconds = allLines(allLines.length - 1).stripPrefix("s:").toInt
      lines = allLines.dropRight(2) // remove last 2 lines
    }

    val rows = lines.length
    val cols = lines.head.length

    // Validate all rows same length
    if (!lines.forall(_.length == cols))
      throw new IllegalArgumentException("Level file is not rectangular")

    // Convert lines into List[List[String]]
    val layout: List[List[String]] = lines.map { line =>
      line.map {
        case '#' => "#"   // mine
        case '-' => "-"   // normal field
        case '!' => "!"   // mine and flagged
        case '?' => "?"   // normal field flagged
        case '+' => "+"   // clicked field
        case other => throw new IllegalArgumentException(
          s"Invalid character '$other' in level file"
        )
      }.toList.map(_.toString)
    }.toList
    println("Layout: " + layout)
    println("Lines: " + lines)

    (new Board(layout), seconds, moves)
  }

//  def loadMovesFromFile(parent: Window, board: Board):Unit = {
//    val file = loadFile(parent)
//    val chooser = new FileChooser(new java.io.File("."))
//    chooser.title = "Select a Moves File"
//
//    if (chooser.showOpenDialog(gamePanel) == FileChooser.Result.Approve) {
//      val file = chooser.selectedFile
//      try {
//        println("Loaded moves at " +  file.getAbsolutePath)
//        playMovesFromFile(file, board)
//      } catch {
//        case ex: Exception =>
//          Dialog.showMessage(
//            parent = this,
//            message = s"Failed to load moves: ${ex.getMessage}",
//            title = "Error",
//            messageType = Dialog.Message.Error
//          )
//      }
//    }
//  }





}
