import scala.swing._
import scala.swing.event._

object Minesweeper extends SimpleSwingApplication {
  val rows = 5
  val cols = 5


  // Grid buttons
  val buttons: Array[Array[Button]] =
    Array.tabulate(rows, cols)((r, c) => new Button("") {

      preferredSize = new Dimension(10, 10)
      minimumSize   = new Dimension(10, 10)
      maximumSize   = new Dimension(10, 10)
    })

  // Menu panel
  val menuPanel = new BoxPanel(Orientation.Vertical) {
    contents += Swing.VStrut(20)

    contents += new Label("Minesweeper") {
      font = new Font("Arial", java.awt.Font.BOLD, 20)
      horizontalAlignment = Alignment.Center
    }
    contents += Swing.VStrut(80)
    contents += new Button("Start Game") {
      reactions += {case ButtonClicked(_) => startGame()}
      preferredSize = new Dimension(400, 50)
      minimumSize   = new Dimension(400, 50)
      maximumSize   = new Dimension(400, 50)
    }
    contents += Swing.VStrut(40)
    contents += new Button("Start Game") {
      reactions += {case ButtonClicked(_) => startGame()}
      preferredSize = new Dimension(400, 50)
      minimumSize   = new Dimension(400, 50)
      maximumSize   = new Dimension(400, 50)
    }
    contents += Swing.VStrut(40)
    contents += new Button("Start Game") {
      reactions += {case ButtonClicked(_) => startGame()}
      preferredSize = new Dimension(400, 50)
      minimumSize   = new Dimension(400, 50)
      maximumSize   = new Dimension(400, 50)
    }
    contents += Swing.VStrut(40)
    contents += new Button("Start Game") {
      reactions += {case ButtonClicked(_) => startGame()}
      preferredSize = new Dimension(400, 50)
      minimumSize   = new Dimension(400, 50)
      maximumSize   = new Dimension(400, 50)
    }
    contents += Swing.VStrut(40)
    // Center horizontally
    contents.foreach {
      case comp: Component => comp.xLayoutAlignment = 0.5
      case _ => // ignore
    }
  }

  // Game panel (grid)
  val gamePanel = new GridPanel(rows, cols) {
    contents ++= buttons.flatten.toSeq
  }

  val frame = new MainFrame {
    title = "Minesweeper"
    contents = menuPanel
    size = new Dimension(500, 500)
  }
  def top = frame

  // Switch to game grid
  def startGame(): Unit = {
    frame.contents = gamePanel
    frame.validate() // refresh the frame to show new contents
    frame.repaint()
  }

  listenTo(buttons.flatten: _*)
  reactions += {
    case ButtonClicked(b) =>
      val pos = for {
        r <- 0 until rows
        c <- 0 until cols
        if buttons(r)(c) == b
      } yield (r, c)
      println(s"Button clicked at $pos")
  }
}
