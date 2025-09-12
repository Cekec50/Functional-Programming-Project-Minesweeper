package ui

import scala.swing._
import scala.swing.event._
import java.io.File
import scala.util.Random

class DifficultyChooserPanel(frame: MainFrameUI) extends BorderPanel {

  // === Title ===
  private val title = new Label("Choose Difficulty") {
    font = new Font("Arial", java.awt.Font.BOLD, 20)
    horizontalAlignment = Alignment.Center
  }

  // === Difficulty selection ===
  private val beginner    = new RadioButton("Beginner")
  private val intermediate = new RadioButton("Intermediate")
  private val expert      = new RadioButton("Expert")

  private val difficultyGroup = new ButtonGroup(beginner, intermediate, expert)

  private val difficultyBox = new BoxPanel(Orientation.Vertical) {
    contents += beginner
    contents += intermediate
    contents += expert

  }

  // === Level list ===
  private val levelList = new ListView[String](Seq.empty)
  private val scrollPane = new ScrollPane(levelList) {
    preferredSize = new Dimension(200, 150)
  }

  // === Buttons ===
  private val playButton = new Button("Play")
  private val backButton = new Button("Back")
  private val buttonBar = new FlowPanel {
    contents += playButton
    contents += backButton
  }

  // === Layout ===
  layout(title)       = BorderPanel.Position.North
  layout(difficultyBox) = BorderPanel.Position.West
  layout(scrollPane)  = BorderPanel.Position.Center
  layout(buttonBar)   = BorderPanel.Position.South

  // === Event handling ===
  listenTo(beginner, intermediate, expert, playButton, backButton)

  reactions += {

    case ButtonClicked(`beginner`) =>
      loadLevels("levels/beginner")
    case ButtonClicked(`intermediate`) =>
      loadLevels("levels/intermediate")
    case ButtonClicked(`expert`) =>
      loadLevels("levels/expert")

    case ButtonClicked(`playButton`) =>
      val difficulty = difficultyGroup.selected.get.text
      val selected = levelList.selection.items.headOption
      selected match {
        case Some("Random Level") =>
          if (levelList.listData.size > 1) {
            val randomLevel = Random.shuffle(levelList.listData.filterNot(_ == "Random Level")).head
            frame.startGame(new File(s"levels/${difficulty.toLowerCase}/$randomLevel"))
          }
        case Some(fileName) =>
          frame.startGame(new File(s"levels/${difficulty.toLowerCase}/$fileName"))
        case None =>
          Dialog.showMessage(this, "Please select a level!", "No Level Selected")
      }

    case ButtonClicked(`backButton`) =>
      frame.showMenu()
  }

  // === Helper: load level list from folder ===
  private def loadLevels(path: String): Unit = {
    val folder = new File(path)
    val files = folder.listFiles().filter(_.isFile).map(_.getName).toSeq.sorted
    levelList.listData = files :+ "Random Level"

  }
}
