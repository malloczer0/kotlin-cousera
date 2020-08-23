package games.gameOfFifteen

import board.Cell
import board.Direction
import board.GameBoard
import board.createGameBoard
import games.game.Game
import games.game2048.addNewValue

/*
 * Implement the Game of Fifteen (https://en.wikipedia.org/wiki/15_puzzle).
 * When you finish, you can play the game by executing 'PlayGameOfFifteen'.
 */
fun newGameOfFifteen(initializer: GameOfFifteenInitializer = RandomGameInitializer()): Game
        = GameOfFifteen(initializer)

class GameOfFifteen (private val initializer: GameOfFifteenInitializer): Game {
    private val board = createGameBoard<Int?>(4)

    override fun canMove(): Boolean = true

    override fun hasWon(): Boolean {
        return board.getAllCells().map { board[it] }.filterNotNull() == (1..15).toList()
    }

    override fun get(i: Int, j: Int): Int? = board[board.getCell(i,j)]

    override fun initialize() {
        board.addValues(initializer)
    }

    override fun processMove(direction: Direction) {
        board.moveValuesFifteen(direction)
    }

    private fun GameBoard<Int?>.addValues(initializer: GameOfFifteenInitializer) {
        board.getAllCells().zip(initializer.initialPermutation) {
            cell, value ->
            board[cell] = value
        }
    }

    private fun GameBoard<Int?>.swapValues(rowToBeUpdated: Cell, emptyCells: List<Cell>) {
        val temp = this[rowToBeUpdated]
        this[rowToBeUpdated] = this[emptyCells.get(0)]
        this[emptyCells.get(0)] = temp
    }

    private fun GameBoard<Int?>.moveValuesFifteen(direction: Direction) {

        val emptyCells = this.getAllCells().filter { it -> this[it] == null }

        when (direction) {
            Direction.LEFT -> {

                val rowToBeUpdated = this.getCell(emptyCells[0].i, emptyCells[0].j + 1)
                swapValues(rowToBeUpdated, emptyCells)
            }
            Direction.RIGHT -> {
                val rowToBeUpdated = this.getCell(emptyCells[0].i, emptyCells[0].j - 1)
                swapValues(rowToBeUpdated, emptyCells)
            }
            Direction.DOWN -> {
                val rowToBeUpdated = this.getCell(emptyCells[0].i - 1, emptyCells[0].j)
                swapValues(rowToBeUpdated, emptyCells)
            }
            Direction.UP -> {
                val rowToBeUpdated = this.getCell(emptyCells[0].i + 1, emptyCells[0].j)
                swapValues(rowToBeUpdated, emptyCells)
            }
        }
    }
}

