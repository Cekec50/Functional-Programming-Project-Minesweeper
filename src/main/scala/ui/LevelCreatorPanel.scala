package ui

import controller.{FileController, LevelCreatorController}
import isometries.IsometryFunction
import model.{Board, Field}

import scala.swing._
import scala.swing.BorderPanel.Position
import scala.swing.event.{ButtonClicked, MouseClicked}

class LevelCreatorPanel(frame: MainFrameUI) extends BorderPanel {
  private var board = new Board(List.fill(10, 10)("-"))

  private val title = new Label("Level Creator") {
    font = new Font("Arial", java.awt.Font.BOLD, 20)
    horizontalAlignment = Alignment.Center
  }
  private val addRowButton = operationButton("Add Row")
  private val addColumnButton = operationButton("Add Column")
  private val deleteRowButton = operationButton("Delete Row")
  private val deleteColumnButton = operationButton("Delete Column")
  private val toggleMineButton = operationButton("Toggle mine")
  private val clearAreaButton = operationButton("Clear Area")

  private val applyButton = new Button("Apply")
  private val inverseButton = new Button("Inverse")

  private val saveLevelButton = new Button("Save Level")
  private val backButton = new Button("Back")

  private val beginner     = new RadioButton("Beginner") {selected = true}
  private val intermediate = new RadioButton("Intermediate")
  private val expert       = new RadioButton("Expert")
  private val difficultyGroup = new ButtonGroup(beginner, intermediate, expert)

  private val isometryRotation = new RadioButton("Rotate") {selected = true}
  private val isometryAxialReflection = new RadioButton("Axial Reflection")
  private val isometryTranslation = new RadioButton("Translate")
  private val isometryCentralSymmetry = new RadioButton("Central Symmetry")

  private val reflectionRow = new RadioButton("Row/Up") {selected = true}
  private val reflectionColumn = new RadioButton("Column/Down")
  private val reflectionMainDiag = new RadioButton("Main Diag/Left")
  private val reflectionSecondDiag = new RadioButton("Second Diag/Right")
  private val isometryGroup = new ButtonGroup(isometryRotation,isometryAxialReflection, isometryTranslation, isometryCentralSymmetry)
  private val reflectionGroup = new ButtonGroup(reflectionRow, reflectionColumn, reflectionMainDiag, reflectionSecondDiag)

  var highlightedFields: List[((Int, Int), Boolean)] = Nil

  private def operationButton(text: String): Button = new Button(text) {
    preferredSize = new Dimension(150, 35)
    maximumSize = preferredSize
    margin = new Insets(5, 10, 5, 10)
  }
  private val difficultyBox = new BoxPanel(Orientation.Vertical) {
    contents += beginner
    contents += new Label("Height/Width/Mines:")
    contents += new Label("5-10/5-10/5-15")
    contents += intermediate
    contents += new Label("Height/Width/Mines:")
    contents += new Label("10-15/10-20/15–50")
    contents += expert
    contents += new Label("Height/Width/Mines:")
    contents += new Label("15-20/20-30/50–120")
    border = Swing.TitledBorder(Swing.EtchedBorder, "Difficulty")
  }


  private val buttonBox = new BoxPanel(Orientation.Vertical) {
    contents += new Label("Operations") {
      xAlignment = Alignment.Center
      border = Swing.EmptyBorder(5, 0, 5, 0)
    }
    contents ++= Seq(addRowButton, addColumnButton, deleteRowButton, deleteColumnButton, toggleMineButton, clearAreaButton)
    border = Swing.TitledBorder(Swing.EtchedBorder, "Map Editing")
    border = Swing.EmptyBorder(10, 10, 10, 10)
  }

  private val isometryBox = new BoxPanel(Orientation.Vertical) {
    contents += new BoxPanel(Orientation.Horizontal) {
      contents ++= Seq(isometryRotation, isometryAxialReflection, isometryTranslation, isometryCentralSymmetry)
    }
    contents += new FlowPanel {
      contents ++= Seq(applyButton, inverseButton)
    }

    border = Swing.TitledBorder(Swing.EtchedBorder, "Isometry")
  }
  private val reflectionBox = new BoxPanel(Orientation.Horizontal) {
    contents ++= Seq(reflectionRow, reflectionColumn, reflectionMainDiag, reflectionSecondDiag)
    border = Swing.TitledBorder(Swing.EtchedBorder, "Reflection Axis")
  }


  private val westBox = new BoxPanel(Orientation.Vertical) {
    contents += difficultyBox
    contents += Swing.VStrut(15)
    contents += buttonBox
    contents += Swing.VStrut(15)
    contents += reflectionBox
    contents += isometryBox
  }

  private val buttonBar = new FlowPanel {
    contents ++= Seq(saveLevelButton, backButton)
  }
  private def unhighlighAll(): Unit ={
    highlightedFields = Nil
    board.fields.flatten.foreach(f => f.unhighlightField())
  }
  private def setNewBoard(newBoard:Board): Unit = {
    unhighlighAll()

    layout -= board
    deafTo(board.fields.flatten: _*)
    deafTo(board.fields.flatten.map(_.mouse.clicks): _*)
    board = newBoard
    layout(board)   = Position.Center
    listenTo(board.fields.flatten: _*)
    listenTo(board.fields.flatten.map(_.mouse.clicks): _*)
    board.revalidate()
    board.repaint()

    board.revealAllMinesLevel

    frame.pack()
  }

  private def saveLevel(): Unit = {
    println(board.countMines())
    difficultyGroup.selected.get.text match {

      case "Beginner" => if(board.rows >= 5 && board.rows <= 10 && board.cols >= 5 && board.cols <= 10 &&
        board.countMines() >= 5 && board.countMines() <= 15)
        FileController.saveLevel(frame, board, "beginner")
      else println(s"Rows: ${board.rows}, Cols: ${board.cols}, Mines: ${board.countMines()}")
      case "Intermediate" => if(board.rows >= 10 && board.rows <= 15 && board.cols >= 10 && board.cols <= 20 &&
        board.countMines() >= 15 && board.countMines() <= 50)
        FileController.saveLevel(frame, board, "intermediate")
      else println(s"Rows: ${board.rows}, Cols: ${board.cols}, Mines: ${board.countMines()}")
      case "Expert" => if(board.rows >= 15 && board.rows <= 20 && board.cols >= 20 && board.cols <= 30 &&
        board.countMines() >= 50 && board.countMines() <= 120)
        FileController.saveLevel(frame, board, "expert")
      else println(s"Rows: ${board.rows}, Cols: ${board.cols}, Mines: ${board.countMines()}")
    }
  }

  private def applyIsometry(isometryFunc: (Board, List[((Int, Int), Boolean)], (Int, Int), Int)
                               => (Board, List[((Int, Int), Boolean)], (Int, Int), Int)): Unit = {
    val pivotField = board.getCoordinatesFromField(board.fields.flatten.find(f => !f.enabled).get)
    val reflection = reflectionGroup.selected.get.text match {
      case "Row/Up" => 0
      case "Column/Down" => 1
      case "Main Diag/Left" => 2
      case "Second Diag/Right" => 3
    }
    val (newBoard, newHighlightedFields, newPivotField, _) = isometryFunc(board, highlightedFields, pivotField, reflection)

    setNewBoard(newBoard)
    highlightedFields = newHighlightedFields
    highlightedFields.foreach { case ((r, c), _) =>
      if (r >= 0 && c >= 0 && r < board.rows && c < board.cols)
        board.fields(r)(c).highlightField()
    }
    board.fields(newPivotField._1)(newPivotField._2).enabled = false
  }

  private def applyTranslation(): Unit = {
    val pivotField = board.getCoordinatesFromField(board.fields.flatten.find(f => !f.enabled).get)
    val (newBoard, newHighlightedFields, newPivotField, _) = reflectionGroup.selected.get.text match {
      case "Row/Up" => IsometryFunction.translateUp(pivotField).apply(board, highlightedFields, pivotField, 0)
      case "Column/Down" => IsometryFunction.translateDown(pivotField).apply(board, highlightedFields, pivotField, 0)
      case "Main Diag/Left" => IsometryFunction.translateLeft(pivotField).apply(board, highlightedFields, pivotField, 1)
      case "Second Diag/Right" => IsometryFunction.translateRight(pivotField).apply(board, highlightedFields, pivotField, 1)
    }

    setNewBoard(newBoard)
    highlightedFields = newHighlightedFields
    highlightedFields.foreach { case ((r, c), _) =>
      if (r >= 0 && c >= 0 && r < board.rows && c < board.cols)
        board.fields(r)(c).highlightField()
    }
    board.fields(newPivotField._1)(newPivotField._2).enabled = false
  }

  layout(title)   = Position.North
  layout(westBox) = Position.West
  layout(buttonBar) = Position.South
  layout(board)   = Position.Center

  listenTo(addRowButton, addColumnButton, deleteRowButton, deleteColumnButton, toggleMineButton, clearAreaButton)
  listenTo(applyButton, inverseButton)
  listenTo(saveLevelButton, backButton)

  listenTo(board.fields.flatten: _*)
  listenTo(board.fields.flatten.map(_.mouse.clicks): _*)

  reactions +={
    case e: MouseClicked if e.peer.getButton == java.awt.event.MouseEvent.BUTTON3 =>
      val field = e.source.asInstanceOf[Field]
      LevelCreatorController.selectField(board, field)
    case ButtonClicked(field: Field) => highlightedFields = board.highlighFields(highlightedFields, field)

    case ButtonClicked(`addRowButton`) => setNewBoard(LevelCreatorController.addRow(board))
    case ButtonClicked(`addColumnButton`) => setNewBoard(LevelCreatorController.addColumn(board))
    case ButtonClicked(`deleteRowButton`) => setNewBoard(LevelCreatorController.deleteRow(board))
    case ButtonClicked(`deleteColumnButton`) => setNewBoard(LevelCreatorController.deleteColumn(board))
    case ButtonClicked(`toggleMineButton`) => setNewBoard(LevelCreatorController.toggleMines(board, highlightedFields))
    case ButtonClicked(`clearAreaButton`) =>  setNewBoard(LevelCreatorController.clearArea(board, highlightedFields))

    case ButtonClicked(`applyButton`) => isometryGroup.selected.get.text match {
      case "Rotate" => applyIsometry(IsometryFunction.rotateExpand.apply)
      case "Axial Reflection" => applyIsometry(IsometryFunction.axialReflection.apply)
      case "Translate" => applyTranslation()
      case "Central Symmetry" => applyIsometry(IsometryFunction.centralSymmetry.apply)
      case _ =>
    }
    case ButtonClicked(`inverseButton`) =>  

    case ButtonClicked(`saveLevelButton`) => saveLevel()
    case ButtonClicked(`backButton`) => frame.showMenu()

  }
}
