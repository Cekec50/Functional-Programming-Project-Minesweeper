package ui

import controller.LevelLoader

import scala.swing._
import scala.swing.event._

class MenuPanel(frame: MainFrameUI) extends BoxPanel(Orientation.Vertical) {

  contents += Swing.VStrut(20)
  contents += new Label("Minesweeper") {
    font = new Font("Arial", java.awt.Font.BOLD, 20)
    horizontalAlignment = Alignment.Center
  }
  contents += Swing.VStrut(80)
  contents += makeMenuButton("Start Game", () => frame.chooseDifficulty())
  contents += Swing.VStrut(40)
  contents += makeMenuButton("Load Level/Load Saved Game", () => frame.loadLevel())
  contents += Swing.VStrut(40)
  contents += makeMenuButton("Create Level", () => println("TODO: Create"))
  contents += Swing.VStrut(40)
  contents += makeMenuButton("Highscores", () => println("TODO: Highscores"))
  contents.foreach {
    case comp: Component => comp.xLayoutAlignment = 0.5
    case _ =>
  }

  private def makeMenuButton(text: String, action: () => Unit): Button = {
    val btn = new Button(text) {
      preferredSize = new Dimension(400, 50)
      minimumSize  = new Dimension(400, 50)
      maximumSize = new Dimension(400, 50)
    }
    listenTo(btn) // MenuPanel listens to this button
    btn.reactions += { case ButtonClicked(_) => action() } // only fire for this button
    btn
  }

}

