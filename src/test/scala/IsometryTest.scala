import controller.FileController
import org.scalatest.funsuite.AnyFunSuite

import java.io.File
import isometries._

import java.awt.Transparency

class IsometryTest extends AnyFunSuite {


  test("Clearing of original fields outside image") {
    val file = new File("src/test/resources/test-level-isometry-1.txt")
    val (board, _ , _) = FileController.loadLevelFromFile(file)
    val highlightedFields = List(((0, 0), true), ((0, 1), false), ((1, 0), true), ((1, 1), true))
    val pivotField = (2, 2)
    assert(board.fields(0)(0).getIsMine)
    assert(!board.fields(0)(1).getIsMine)
    assert(board.fields(1)(0).getIsMine)
    assert(board.fields(1)(1).getIsMine)

    val rotation = Rotation()
    val (newBoard, newhighlightedFields, _, _) = rotation(board, highlightedFields, pivotField, 0)
    //println(newhighlightedFields)
    assert(!newBoard.fields(0)(0).getIsMine)
    assert(!newBoard.fields(0)(1).getIsMine)
    assert(!newBoard.fields(1)(0).getIsMine)
    assert(!newBoard.fields(1)(1).getIsMine)
  }
  // --- Rotation ---
  test("Rotate 90° clockwise ") {
    val file = new File("src/test/resources/test-level-isometry-1.txt")
    val (board, _ , _) = FileController.loadLevelFromFile(file)
    val highlightedFields = List(((0, 0), true), ((0, 1), false), ((1, 0), true), ((1, 1), true))
    val pivotField = (2, 2)
    assert(board.fields(0)(0).getIsMine)
    assert(!board.fields(0)(1).getIsMine)
    assert(board.fields(1)(0).getIsMine)
    assert(board.fields(1)(1).getIsMine)

    val rotation = Rotation()
    val (newBoard, newhighlightedFields, _, _) = rotation(board, highlightedFields, pivotField, 0)
    //println(newhighlightedFields)
    assert(newBoard.fields(0)(3).getIsMine)
    assert(newBoard.fields(0)(4).getIsMine)
    assert(newBoard.fields(1)(3).getIsMine)
    assert(!newBoard.fields(1)(4).getIsMine)
  }
  test("Rotate 90° counterclockwise ") {
    val file = new File("src/test/resources/test-level-isometry-2.txt")
    val (board, _ , _) = FileController.loadLevelFromFile(file)
    val highlightedFields = List(((0, 9), true), ((0, 8), false), ((1, 9), true), ((1, 8), true))
    val pivotField = (2, 7)
    assert(board.fields(0)(9).getIsMine)
    assert(!board.fields(0)(8).getIsMine)
    assert(board.fields(1)(9).getIsMine)
    assert(board.fields(1)(8).getIsMine)

    val rotation = new Rotation()
    val (newBoard, newhighlightedFields, _, _) = rotation.inverse(board, highlightedFields, pivotField, 0)
    //println(newhighlightedFields)
    assert(newBoard.fields(0)(5).getIsMine)
    assert(newBoard.fields(0)(6).getIsMine)
    assert(newBoard.fields(1)(6).getIsMine)
    assert(!newBoard.fields(1)(5).getIsMine)
  }

  // --- Reflection ---
  test("Reflection across horizontal axis") {
    val file = new File("src/test/resources/test-level-isometry-1.txt")
    val (board, _ , _) = FileController.loadLevelFromFile(file)
    val highlightedFields = List(((0, 0), true), ((0, 1), false), ((1, 0), true), ((1, 1), true))
    val pivotField = (2, 2)
    val reflection = 0
    assert(board.fields(0)(0).getIsMine)
    assert(!board.fields(0)(1).getIsMine)
    assert(board.fields(1)(0).getIsMine)
    assert(board.fields(1)(1).getIsMine)

    val axialReflection = AxialReflection()
    val (newBoard, newhighlightedFields, _, _) = axialReflection(board, highlightedFields, pivotField, reflection)
    //println(newhighlightedFields)

    assert(newBoard.fields(4)(0).getIsMine)
    assert(newBoard.fields(3)(1).getIsMine)
    assert(newBoard.fields(3)(0).getIsMine)
    assert(!newBoard.fields(4)(1).getIsMine)
  }
  test("Reflection across vertical axis") {
    val file = new File("src/test/resources/test-level-isometry-1.txt")
    val (board, _ , _) = FileController.loadLevelFromFile(file)
    val highlightedFields = List(((0, 0), true), ((0, 1), false), ((1, 0), true), ((1, 1), true))
    val pivotField = (2, 2)
    val reflection = 1
    assert(board.fields(0)(0).getIsMine)
    assert(!board.fields(0)(1).getIsMine)
    assert(board.fields(1)(0).getIsMine)
    assert(board.fields(1)(1).getIsMine)

    val axialReflection = AxialReflection()
    val (newBoard, newhighlightedFields, _, _) = axialReflection(board, highlightedFields, pivotField, reflection)
    //println(newhighlightedFields)

    assert(newBoard.fields(1)(4).getIsMine)
    assert(newBoard.fields(1)(3).getIsMine)
    assert(newBoard.fields(0)(4).getIsMine)
    assert(!newBoard.fields(0)(3).getIsMine)
  }
  test("Reflection across main diagonal") {
    val file = new File("src/test/resources/test-level-isometry-2.txt")
    val (board, _ , _) = FileController.loadLevelFromFile(file)
    val highlightedFields = List(((0, 9), true), ((0, 8), false), ((1, 9), true), ((1, 8), true))
    val pivotField = (2, 7)
    val reflection = 2
    assert(board.fields(0)(9).getIsMine)
    assert(!board.fields(0)(8).getIsMine)
    assert(board.fields(1)(9).getIsMine)
    assert(board.fields(1)(8).getIsMine)

    val axialReflection = AxialReflection()
    val (newBoard, newhighlightedFields, _, _) = axialReflection(board, highlightedFields, pivotField, reflection)
    //println(newhighlightedFields)
    assert(newBoard.fields(4)(6).getIsMine)
    assert(newBoard.fields(3)(6).getIsMine)
    assert(newBoard.fields(4)(5).getIsMine)
    assert(!newBoard.fields(3)(5).getIsMine)
  }
  test("Reflection across second diagonal") {
    val file = new File("src/test/resources/test-level-isometry-1.txt")
    val (board, _ , _) = FileController.loadLevelFromFile(file)
    val highlightedFields = List(((0, 0), true), ((0, 1), false), ((1, 0), true), ((1, 1), true))
    val pivotField = (2, 2)
    val reflection = 3
    assert(board.fields(0)(0).getIsMine)
    assert(!board.fields(0)(1).getIsMine)
    assert(board.fields(1)(0).getIsMine)
    assert(board.fields(1)(1).getIsMine)

    val axialReflection = AxialReflection()
    val (newBoard, newhighlightedFields, _, _) = axialReflection(board, highlightedFields, pivotField, reflection)
    //println(newhighlightedFields)

    assert(newBoard.fields(4)(4).getIsMine)
    assert(newBoard.fields(3)(3).getIsMine)
    assert(newBoard.fields(4)(4).getIsMine)
    assert(!newBoard.fields(3)(4).getIsMine)
  }

  // --- Transparency and Coverage ---
  test("Transparent isometry overwrites mines") {
    val file = new File("src/test/resources/test-level-isometry-3.txt")
    val (board, _ , _) = FileController.loadLevelFromFile(file)
    val highlightedFields = List(((0, 0), true), ((0, 1), false), ((1, 0), true), ((1, 1), true))
    val pivotField = (2, 2)
    assert(board.fields(0)(0).getIsMine)
    assert(!board.fields(0)(1).getIsMine)
    assert(board.fields(1)(0).getIsMine)
    assert(board.fields(1)(1).getIsMine)

    val rotation = new Rotation() with Transparent
    val (newBoard, newhighlightedFields, _, _) = rotation(board, highlightedFields, pivotField, 0)
    //println(newhighlightedFields)
    assert(newBoard.fields(0)(3).getIsMine)
    assert(newBoard.fields(0)(4).getIsMine)
    assert(newBoard.fields(1)(3).getIsMine)
    assert(!newBoard.fields(1)(4).getIsMine)
  }
  test("Non-transparent isometry merges mines") {
    val file = new File("src/test/resources/test-level-isometry-3.txt")
    val (board, _ , _) = FileController.loadLevelFromFile(file)
    val highlightedFields = List(((0, 0), true), ((0, 1), false), ((1, 0), true), ((1, 1), true))
    val pivotField = (2, 2)
    assert(board.fields(0)(0).getIsMine)
    assert(!board.fields(0)(1).getIsMine)
    assert(board.fields(1)(0).getIsMine)
    assert(board.fields(1)(1).getIsMine)

    val rotation = new Rotation() with NonTransparent
    val (newBoard, newhighlightedFields, _, _) = rotation(board, highlightedFields, pivotField, 0)
    //println(newhighlightedFields)
    assert(newBoard.fields(0)(3).getIsMine)
    assert(newBoard.fields(0)(4).getIsMine)
    assert(newBoard.fields(1)(3).getIsMine)
    assert(newBoard.fields(1)(4).getIsMine)
  }

  // --- Extensibility ---
7  test("Expanding isometries expands map") {
    val file = new File("src/test/resources/test-level-isometry-2.txt")
    val (board, _ , _) = FileController.loadLevelFromFile(file)
    val highlightedFields = List(((0, 9), true), ((0, 8), false), ((1, 9), true), ((1, 8), true))
    val pivotField = (0, 9)
    val reflection = 2

    val axialReflection = new AxialReflection() with Expandable
    val (newBoard, newhighlightedFields, _, _) = axialReflection(board, highlightedFields, pivotField, reflection)
    //println(newhighlightedFields)
    assert(newBoard.rows == board.rows + 1)
    assert(newBoard.cols == board.cols + 1)
  }
  test("Non-expanding isometries doesn't expand map") {
    val file = new File("src/test/resources/test-level-isometry-2.txt")
    val (board, _ , _) = FileController.loadLevelFromFile(file)
    val highlightedFields = List(((0, 9), true), ((0, 8), false), ((1, 9), true), ((1, 8), true))
    val pivotField = (0, 9)
    val reflection = 2

    val axialReflection = new AxialReflection() with NonExpandable
    val (newBoard, newhighlightedFields, _, _) = axialReflection(board, highlightedFields, pivotField, reflection)
    //println(newhighlightedFields)
    assert(newBoard.rows == board.rows)
    assert(newBoard.cols == board.cols)
  }

  // --- Composition of Isometries ---
  test("Central symmetry") {

    val file = new File("src/test/resources/test-level-isometry-2.txt")
    val (board, _ , _) = FileController.loadLevelFromFile(file)
    val highlightedFields = List(((0, 9), true), ((0, 8), false), ((1, 9), true), ((1, 8), true))
    val pivotField = (2, 7)
    val reflection = 2
    assert(board.fields(0)(9).getIsMine)
    assert(!board.fields(0)(8).getIsMine)
    assert(board.fields(1)(9).getIsMine)
    assert(board.fields(1)(8).getIsMine)

    val centralSymmetry = IsometryFunction.centralSymmetry
    val (newBoard, newhighlightedFields, _, _) = centralSymmetry(board, highlightedFields, pivotField, reflection)
    //println(newhighlightedFields)
    assert(newBoard.fields(4)(5).getIsMine)
    assert(!newBoard.fields(4)(6).getIsMine)
    assert(newBoard.fields(3)(5).getIsMine)
    assert(newBoard.fields(3)(6).getIsMine)
  }
  test("Translation") {
    val file = new File("src/test/resources/test-level-isometry-1.txt")
    val (board, _ , _) = FileController.loadLevelFromFile(file)
    val highlightedFields = List(((0, 0), true), ((0, 1), false), ((1, 0), true), ((1, 1), true))
    val pivotField = (2, 2)
    val reflection = 1
    assert(board.fields(0)(0).getIsMine)
    assert(!board.fields(0)(1).getIsMine)
    assert(board.fields(1)(0).getIsMine)
    assert(board.fields(1)(1).getIsMine)

    val translation = IsometryFunction.translateRight(pivotField)
    val (newBoard, _, _, _) = translation(board, highlightedFields, pivotField, reflection)

    assert(newBoard.fields(0)(2).getIsMine)
    assert(!newBoard.fields(0)(3).getIsMine)
    assert(newBoard.fields(1)(2).getIsMine)
    assert(newBoard.fields(1)(3).getIsMine)
  }


  // --- Inverse and Quasi-Inverse ---
  test("Applying isometry and its inverse restores original") {
    val file = new File("src/test/resources/test-level-isometry-1.txt")
    val (board, _ , _) = FileController.loadLevelFromFile(file)
    val highlightedFields = List(((0, 0), true), ((0, 1), false), ((1, 0), true), ((1, 1), true))
    val pivotField = (2, 2)
    assert(board.fields(0)(0).getIsMine)
    assert(!board.fields(0)(1).getIsMine)
    assert(board.fields(1)(0).getIsMine)
    assert(board.fields(1)(1).getIsMine)

    val rotation = Rotation()
    val inverseRotation = Rotation().inverse
    val (newBoard, newhighlightedFields, _, _) = rotation(board, highlightedFields, pivotField, 0)
    val (newNewBoard, _, _, _) = inverseRotation(newBoard, newhighlightedFields, pivotField, 0)
    assert(newNewBoard.fields(0)(0).getIsMine)
    assert(!newNewBoard.fields(0)(1).getIsMine)
    assert(newNewBoard.fields(1)(0).getIsMine)
    assert(newNewBoard.fields(1)(1).getIsMine)
  }
}