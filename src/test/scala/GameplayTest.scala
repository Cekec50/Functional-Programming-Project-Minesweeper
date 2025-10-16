import controller.{FileController, GameController}
import model.{Board, Click}
import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite

import java.io.File

class GameplayTest extends AnyFunSuite with BeforeAndAfter {
  var gameController: GameController = _
  var board: Board = _
  var board2: Board = _
  before{
    gameController = new GameController(() => (), () => (), () => ())
    val file = new File("src/test/resources/test-level-2.txt")
    val (boardTmp, _ , _) = FileController.loadLevelFromFile(file)
    board = boardTmp
    val file2 = new File("src/test/resources/test-level-3.txt")
    val (boardTmp2, _ , _) = FileController.loadLevelFromFile(file2)
    board2 = boardTmp2
  }
  // --- Gameplay: Revealing Fields ---
  test("Reveal non-mine field shows correct adjacent count") {

    gameController.playMove(Click.Left, board, board.fields(0)(0))
    gameController.playMove(Click.Left, board, board.fields(2)(2))
    gameController.playMove(Click.Left, board, board.fields(5)(0))

    assert(board.fields(0)(0).text == "1")
    assert(board.fields(2)(2).text == "8")
    assert(board.fields(5)(0).text == "")

  }
  test("Reveal mine ends game") {
    gameController.playMove(Click.Left, board, board.fields(1)(1)) // click on mine field
    assert(gameController.getGameState) // if true, game over

    assert(board.fields(0)(0).enabled)
    assert(board.fields(0)(1).enabled)
    gameController.playMove(Click.Left, board, board.fields(0)(0))
    gameController.playMove(Click.Left, board, board.fields(0)(1))
    assert(board.fields(0)(0).enabled)
    assert(board.fields(0)(1).enabled)
  }
  test("Reveal empty field auto-reveals region") {

    assert(board.fields(4)(0).text == "")
    assert(board.fields(4)(1).text == "")
    assert(board.fields(5)(0).text == "")
    assert(board.fields(2)(2).text == "")

    gameController.playMove(Click.Left, board, board.fields(5)(0))

    assert(board.fields(4)(0).text == "1")
    assert(board.fields(4)(1).text == "2")
    assert(board.fields(5)(0).text == "")
    assert(board.fields(2)(2).text == "")
  }
  test("Reveal already revealed field has no effect") {

    assert(board.fields(2)(2).text == "")
    assert(board.fields(2)(2).enabled)

    gameController.playMove(Click.Left, board, board.fields(2)(2))

    assert(board.fields(2)(2).text == "8")
    assert(!board.fields(2)(2).enabled)

    gameController.playMove(Click.Left, board, board.fields(2)(2))

    assert(board.fields(2)(2).text == "8")
    assert(!board.fields(2)(2).enabled)

  }
  test("Left-click on flagged field does nothing") {

    assert(board.fields(2)(2).text == "")
    assert(board.fields(2)(2).enabled)

    gameController.playMove(Click.Right, board, board.fields(2)(2))

    gameController.playMove(Click.Left, board, board.fields(2)(2))

    assert(board.fields(2)(2).text == "F")
    assert(board.fields(2)(2).enabled)
  }

  // --- Gameplay: Flagging Fields ---
  test("Right-click flags a field") {

    assert(board.fields(2)(2).text == "")
    assert(board.fields(2)(2).enabled)

    gameController.playMove(Click.Right, board, board.fields(2)(2))

    assert(board.fields(2)(2).text == "F")
    assert(board.fields(2)(2).enabled)


  }
  test("Right-click removes flag") {

    assert(board.fields(2)(2).text == "")
    assert(board.fields(2)(2).enabled)

    gameController.playMove(Click.Right, board, board.fields(2)(2))

    assert(board.fields(2)(2).text == "F")
    assert(board.fields(2)(2).enabled)

    gameController.playMove(Click.Right, board, board.fields(2)(2))

    assert(board.fields(2)(2).text == "")
    assert(board.fields(2)(2).enabled)
  }
  test("Game victory detected when all non-mines revealed") {
    gameController.playMove(Click.Left, board2, board2.fields(0)(0)) //This should win the game

    assert(gameController.getGameState) // If true, game is over
    assert(!board2.fields.flatten.exists(f => f.enabled)) // All buttons should be disabled
  }

  // --- Move Sequences (Scripted Play) ---
  test("Applying valid sequence yields expected results") {
    val file = new File("src/test/resources/test-moves.txt")
    gameController.playMovesFromGivenFile(file, board)

    assert(board.fields(2)(2).text == "8")
    assert(!board.fields(2)(2).enabled)

    assert(board.fields(2)(4).text == "3")
    assert(!board.fields(2)(2).enabled)

    assert(board.fields(1)(4).text == "2")
    assert(!board.fields(2)(2).enabled)

    assert(board.fields(0)(0).text == "F")
    assert(!board.fields(2)(2).enabled)

    assert(board.fields(0)(1).text == "F")
    assert(!board.fields(2)(2).enabled)

    assert(board.fields(0)(2).text == "F")
    assert(!board.fields(2)(2).enabled)
  }
  test("Invalid move syntax is rejected") {
    intercept[Exception] {
      val file = new File("src/test/resources/test-moves-bad.txt")
      gameController.playMovesFromGivenFile(file, board)
    }
  }
  test("Sequence stops if a mine is revealed") {
    val file = new File("src/test/resources/test-moves-2.txt")
    assert(board.fields(0)(0).enabled)
    assert(board.fields(0)(1).enabled)

    gameController.playMovesFromGivenFile(file, board)

    assert(gameController.getGameState) // if true game over
    assert(board.fields(0)(0).enabled)
    assert(board.fields(0)(1).enabled)
  }

  // --- Hint System ---
  test("Help suggests valid safe move") {
    val suggestions = Seq.fill(20)(gameController.suggestMove(board))
    assert(suggestions.forall(f => !f.getIsMine), "Some suggested fields contained mines")
  }

  test("Help suggests random valid moves") {
    val suggestions = Seq.fill(10)(gameController.suggestMove(board))
    assert(suggestions.toSet.size > 1, "suggestMove is not random — all moves are identical")
  }
}
