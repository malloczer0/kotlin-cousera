package board

import board.Direction.*

open class BoardImpl(override val width: Int): SquareBoard {

    protected open val cells: List<Cell> = (1..width).flatMap { i: Int ->
        (1..width).map { j -> Cell(i, j) }
    }

    override fun getCellOrNull(i: kotlin.Int, j: kotlin.Int): board.Cell?
            = if (i in 1..width && j in 1..width ) getCell(i,j) else null

    override fun getCell(i: Int, j: Int): Cell
            = if (i in 1..width && j in 1..width ) cells.first{ it == Cell(i,j) } else throw IllegalArgumentException()

    override fun getAllCells(): Collection<Cell> = cells

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> = cells.filter { it.i == i }.slice(run {
        var sliceRange: IntProgression = when {
            jRange.first < jRange.last -> jRange.first - 1 until jRange.last
            else -> jRange.last - 1 downTo jRange.first - 1
        }

        sliceRange = if (sliceRange.step > 0)
            (maxOf(sliceRange.first, 0)..minOf(sliceRange.last, width - 1))
        else
            (minOf(sliceRange.first, width - 1)..maxOf(sliceRange.last, 0)).reversed()

        sliceRange
    })

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> = cells.filter { it.j == j }.slice(run {
        var sliceRange: IntProgression = when {
            iRange.first < iRange.last -> iRange.first - 1 until iRange.last
            else -> iRange.last - 1 downTo iRange.first - 1
        }

        sliceRange = if (sliceRange.step > 0)
            (maxOf(sliceRange.first, 0)..minOf(sliceRange.last, width - 1))
        else
            (minOf(sliceRange.first, width - 1)..maxOf(sliceRange.last, 0)).reversed()

        sliceRange
    })

    override fun Cell.getNeighbour(direction: Direction): Cell? = when (direction) {
        UP -> getCellOrNull(i - 1, j)
        LEFT -> getCellOrNull(i, j - 1)
        DOWN -> getCellOrNull(i + 1, j)
        RIGHT -> getCellOrNull(i, j + 1)
    }
}

class GameBoardImpl<T>(override val width: Int): BoardImpl(width), GameBoard<T> {

    override var cells: List<Cell> = (1..width).flatMap { i: Int ->
        (1..width).map { j -> Cell(i, j) }
    }

    private val gameCells = mutableMapOf<Cell, T?>()

    override fun get(cell: Cell): T? {
        return gameCells[cell] ?: null
    }

    override fun set(cell: Cell, value: T?) {
        gameCells[cell] = value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> {
        return gameCells.keys.filter(cellMatcher(predicate))
    }

    override fun find(predicate: (T?) -> Boolean): Cell? {
        return gameCells.keys.find(cellMatcher(predicate))
    }

    override fun all(predicate: (T?) -> Boolean): Boolean {
        // ez way to compare cuz of Cell.toString()
        return if (getAllCells().toString() == gameCells.keys.toString())
            gameCells.keys.all(cellMatcher(predicate))
        else
            false
    }

    override fun any(predicate: (T?) -> Boolean): Boolean {
        // should handle uninitialised (nulls) separately
        return if (gameCells.keys.any(cellMatcher(predicate)))
            true
        else {
            cells.filterNot { it in gameCells.keys }.any(cellMatcher(predicate))
        }
    }

    private fun cellMatcher(predicate: (T?) -> Boolean): (Cell) -> Boolean {
        return { cell -> predicate(gameCells[cell]) }
    }
}

fun createSquareBoard(width: Int): SquareBoard = BoardImpl(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl(width)

operator fun <T> GameBoard<T>.get(i: Int, j: Int) = get(getCell(i, j))
operator fun <T> GameBoard<T>.set(i: Int, j: Int, value: T) = set(getCell(i, j), value)

fun main() {
    val squareBoard = createSquareBoard(2)
    val gameBoard = createGameBoard<Int>(2)
    gameBoard[1, 1] = 1
    gameBoard[1, 2] = 0
    gameBoard[2, 1] = 2
    gameBoard[2, 2] = 3
    println(gameBoard.all { it != 0 })
    println(!gameBoard.any { it == 0 })
}
