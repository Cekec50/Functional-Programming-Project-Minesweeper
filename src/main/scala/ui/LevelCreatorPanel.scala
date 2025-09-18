package ui

import model.Board
import scala.swing._
import scala.swing.BorderPanel.Position

class LevelCreatorPanel(frame: MainFrameUI, board: Board) extends BorderPanel {

  // === Title ===
  private val title = new Label("Level Creator") {
    font = new Font("Arial", java.awt.Font.BOLD, 20)
    horizontalAlignment = Alignment.Center
  }

  // === Difficulty selection ===
  private val beginner     = new RadioButton("Beginner")
  private val intermediate = new RadioButton("Intermediate")
  private val expert       = new RadioButton("Expert")
  private val difficultyGroup = new ButtonGroup(beginner, intermediate, expert)

  private val difficultyBox = new BoxPanel(Orientation.Vertical) {
    contents += new Label("Choose Difficulty") {
      xAlignment = Alignment.Center
      border = Swing.EmptyBorder(5, 0, 5, 0)
    }
    contents ++= Seq(beginner, intermediate, expert)
    border = Swing.TitledBorder(Swing.EtchedBorder, "Difficulty")
  }

  // === Operations (uniform button size) ===
  private def operationButton(text: String): Button = new Button(text) {
    preferredSize = new Dimension(150, 35)
    maximumSize = preferredSize
    margin = new Insets(5, 10, 5, 10)
  }

  private val buttonBox = new BoxPanel(Orientation.Vertical) {
    contents += new Label("Operations") {
      xAlignment = Alignment.Center
      border = Swing.EmptyBorder(5, 0, 5, 0)
    }
    contents ++= Seq(
      operationButton("Add row"),
      operationButton("Add column"),
      operationButton("Delete row"),
      operationButton("Delete column"),
      operationButton("Change field type"),
      operationButton("Clear area")
    )
    border = Swing.TitledBorder(Swing.EtchedBorder, "Map Editing")
    border = Swing.EmptyBorder(10, 10, 10, 10)
  }

  // === Isometries ===
  private val levelList = new ListView[String](Seq.empty)
  private val scrollPanel = new ScrollPane(levelList) {
    preferredSize = new Dimension(200, 150)
  }

  private val isometryBox = new BoxPanel(Orientation.Vertical) {
    contents += new Label("Choose Isometry") {
      xAlignment = Alignment.Center
      border = Swing.EmptyBorder(5, 0, 5, 0)
    }
    contents += scrollPanel
    contents += new FlowPanel{
      contents += new Button("Apply")
      contents += new Button("Inverse")
    }
    border = Swing.TitledBorder(Swing.EtchedBorder, "Isometries")
    border = Swing.EmptyBorder(10, 10, 10, 10)
  }

  private val westBox = new BoxPanel(Orientation.Vertical) {
    contents += difficultyBox
    contents += Swing.VStrut(15)
    contents += buttonBox
    contents += Swing.VStrut(15)
    contents += isometryBox
  }

  // === Bottom buttons ===
  private val buttonBar = new FlowPanel {
    contents += new Button("Create Level")
    contents += new Button("Back")
  }

  // === Layout ===
  layout(title)   = Position.North
  layout(westBox) = Position.West
  layout(buttonBar) = Position.South
  layout(board)   = Position.Center
}
