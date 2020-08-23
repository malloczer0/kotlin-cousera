package board

import board.Direction.*

open class SquareBoardImplementation(override val width: Int) : SquareBoard {

    protected val cells: Set<Cell> = generateCells()

    fun generateCells() = (1..width).flatMap { i: Int ->
        (1..width).map { j -> Cell(i, j) }
    }.toSet()

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

class GameBoardImplementation<T>(private val widthOfBoard: Int): SquareBoardImplementation(widthOfBoard), GameBoard<T> {

    private val dateCellsMap = mutableMapOf<Cell, T?>()

    init {
        cells.forEach { dateCellsMap[it] = null }
    }

    override fun get(cell: Cell): T? {
        return dateCellsMap[cell] ?: null
    }

    override fun set(cell: Cell, value: T?) {
        dateCellsMap[cell] = value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell>
            = dateCellsMap.filterValues(predicate).keys

    override fun find(predicate: (T?) -> Boolean): Cell?
            = dateCellsMap.keys.find { cell -> predicate(dateCellsMap[cell]) }

    override fun any(predicate: (T?) -> Boolean): Boolean
            = dateCellsMap.values.any(predicate)

    override fun all(predicate: (T?) -> Boolean): Boolean
            = dateCellsMap.values.all(predicate)
}

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImplementation(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImplementation(width)

operator fun <T> GameBoard<T>.get(i: Int, j: Int) = get(getCell(i, j))
operator fun <T> GameBoard<T>.set(i: Int, j: Int, value: T) = set(getCell(i, j), value)

