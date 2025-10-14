package isometries

import controller.LevelCreatorController
import model.Board

import scala.collection.immutable.List

trait Expandable extends Isometry {

  abstract override def apply(board: Board,
                               highlightedFields: List[((Int, Int), Boolean)],
                               pivotField: (Int, Int),
                               reflection: Int):
  (Board, List[((Int, Int), Boolean)], (Int, Int), Int) = {


    val (transformedBoard, transformedFields, newPivot, newReflection) = super.apply(board, highlightedFields, pivotField, reflection)

    val (expandedBoard, shiftedFields, shiftedPivot) = extendBoard(transformedBoard, transformedFields, newPivot)

    (expandedBoard, shiftedFields, shiftedPivot, newReflection)
  }
  def extendBoard(board: Board, highlightedFields: List[((Int, Int), Boolean)], pivot: (Int, Int)):
  (Board, List[((Int, Int), Boolean)], (Int, Int)) = {


    // Compute max coordinates from highlighted fields
    val maxRow = highlightedFields.map(_._1._1).maxOption.getOrElse(board.rows - 1)
    val maxCol = highlightedFields.map(_._1._2).maxOption.getOrElse(board.cols - 1)
    val minRow = highlightedFields.map(_._1._1).minOption.getOrElse(0)
    val minCol = highlightedFields.map(_._1._2).minOption.getOrElse(0)

    // Compute how many rows / columns to add
    val rowsToAdd = (maxRow + 1 - board.rows).max(0) + (-minRow).max(0)
    val colsToAdd = (maxCol + 1 - board.cols).max(0) + (-minCol).max(0)

    var layout = board.getLayout(true)


    for (_ <- 0 until rowsToAdd) {
      layout = layout :+ List.fill(board.cols + colsToAdd)("-")
    }
    // Add columns
    for (_ <- 0 until colsToAdd) {
      layout = layout.zip(List.fill(board.rows + rowsToAdd)("-")).map { case (row, v) => row :+ v }
    }
    val shiftedFields = highlightedFields.map { case ((x, y), v) =>
      ((x + (-minRow).max(0), y + (-minCol).max(0)), v)
    }
    val returnPivot = (pivot._1 + (-minRow).max(0), pivot._2 + (-minCol).max(0))
    val returnBoard = LevelCreatorController.updateBoardWithHighlightedFields(new Board(layout), shiftedFields)
    (returnBoard, shiftedFields, returnPivot)

  }

}
