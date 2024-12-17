import java.util.*

fun main() = day(12) {
    part1 { input ->
        expectSample(1930)

        val visited = Array(input.size) { BooleanArray(input.first().length) { false } }
    
        var totalPrice = 0L
        for ((r, row) in input.withIndex()) {
            for ((c, char) in row. withIndex()) {
                if (visited[r][c]) continue

                val queue: Queue<PlotCoordinate> = LinkedList()
                queue.offer(PlotCoordinate(r, c))

                var regionArea = 0
                var regionPerimeter = 0
                while (queue.isNotEmpty()) {
                    val currentCoordinate = queue.poll()
                    val (currentRow, currentColumn) = currentCoordinate
                    val currentValue = input[currentRow][currentColumn]

                    if (visited[currentRow][currentColumn]) continue

                    regionArea++
                    regionPerimeter += calculatePlotPerimeter(input, currentCoordinate)

                    visited[currentRow][currentColumn] = true

                    straight2DDirections
                        .map { (dR, dC) -> PlotCoordinate(currentRow + dR, currentColumn + dC) }
                        .filter { (r, c) -> r in 0..<input.size && c in 0..<input.first().length }
                        .filter { (r, c) -> input[r][c] == currentValue}
                        .filter { (r, c) -> !visited[r][c] }
                        .forEach(queue::offer)

                }

                totalPrice += (regionArea * regionPerimeter)
            }
        }
    
        totalPrice
    }

    part2 { input ->
        expectSample(1206)
    
        val visited = Array(input.size) { BooleanArray(input.first().length) { false } }
    
        var totalPrice = 0L
        for ((r, row) in input.withIndex()) {
            for ((c, char) in row. withIndex()) {
                if (visited[r][c]) continue

                val queue: Queue<PlotCoordinate> = LinkedList()
                queue.offer(PlotCoordinate(r, c))

                var regionArea = 0
                var sides: Int? = null
                while (queue.isNotEmpty()) {
                    val currentCoordinate = queue.poll()
                    val (currentRow, currentColumn) = currentCoordinate
                    val currentValue = input[currentRow][currentColumn]

                    if (visited[currentRow][currentColumn]) continue

                    regionArea++
                    if (sides == null) {
                        sides = calculateSides(input, currentCoordinate)
                    }

                    visited[currentRow][currentColumn] = true

                    straight2DDirections
                        .map { (dR, dC) -> PlotCoordinate(currentRow + dR, currentColumn + dC) }
                        .filter { (r, c) -> r in 0..<input.size && c in 0..<input.first().length }
                        .filter { (r, c) -> input[r][c] == currentValue}
                        .filter { (r, c) -> !visited[r][c] }
                        .forEach(queue::offer)

                }

                totalPrice += (regionArea * sides!!)
            }
        }
    
        totalPrice
    }
}

private fun calculatePlotPerimeter(input: List<String>, coordinate: PlotCoordinate): Int {
    val value = input[coordinate.r][coordinate.c]
    val adjacentRegionPlots = straight2DDirections
        .map { (dR, dC) -> PlotCoordinate(coordinate.r + dR, coordinate.c + dC) }
        .filter { (r, c) -> r in 0..<input.size && c in 0..<input.first().length }
        .filter { (r, c) -> input[r][c] == value }
    return 4 - adjacentRegionPlots.size
}
private fun printMap(
    input: List<String>,
    coordinate: PlotCoordinate,
    facing: Facing,
    char: Char? = null,
) {
    val value = input[coordinate.r][coordinate.c]
    if (char == null || value == char) {
        println("$facing -----------------------")
        println(
            input.mapIndexed { r, row ->
                row.mapIndexed { c, char ->
                    when {
                        r == coordinate.r && c == coordinate.c -> when (facing) {
                            Facing.NORTH -> '^'
                            Facing.EAST -> '>'
                            Facing.SOUTH -> 'v'
                            Facing.WEST -> '<'
                        }
                        else -> if (char == value) char else '.'
                    }
                }.joinToString("")
            }.joinToString("\n")
        )
    }
}

private tailrec fun calculateSides(
    input: List<String>,
    coordinate: PlotCoordinate,
    sides: Int = 0,
    facing: Facing = Facing.EAST,
    originalCoordinate: PlotCoordinate = coordinate,
    firstPlot: Boolean = true,
): Int {

    if (!firstPlot && coordinate == originalCoordinate) return sides.coerceAtLeast(4)


    val left = facing.getLeft()
    val right = facing.getLeft().getLeft().getLeft()
    val back = facing.getLeft().getLeft()

    var nextCoordinate = coordinate
    while (nextCoordinate.canMove(input, facing)) {
        nextCoordinate = nextCoordinate.move(facing)

        if (!firstPlot && coordinate == originalCoordinate) return sides.coerceAtLeast(4)

        if (nextCoordinate.canMove(input, left)) {
            return calculateSides(input, nextCoordinate, sides + 1, left, originalCoordinate, false)
        }
    }

    if (nextCoordinate.canMove(input, left)) {
        return calculateSides(input, nextCoordinate, sides + 1, left, originalCoordinate, false)
    }

    if (nextCoordinate.canMove(input, right)) {
        val isFirstPlot = firstPlot && coordinate == originalCoordinate
        return calculateSides(input, nextCoordinate, sides + 1, right, originalCoordinate, isFirstPlot)
    }

    if (nextCoordinate.canMove(input, back)) {
        val sidesDelta = if (nextCoordinate == originalCoordinate && facing != Facing.WEST) 1 else 2
        return calculateSides(input, nextCoordinate, sides + sidesDelta, back, originalCoordinate, false)
    }

    return calculateSides(input, nextCoordinate, sides, back, originalCoordinate, false)
}

private enum class Facing(val dR: Int, val dC: Int) {
    NORTH(-1, 0),
    EAST(0, 1),
    SOUTH(1, 0),
    WEST(0, -1);

    fun getLeft(): Facing = when (this) {
        NORTH -> WEST
        WEST -> SOUTH
        SOUTH -> EAST
        EAST -> NORTH
    }
}

private fun PlotCoordinate.canMove(input: List<String>, facing: Facing, f: (PlotCoordinate) -> Boolean = { true }): Boolean {
    val plotValue = input[r][c]

    val nextRow = r + facing.dR
    val nextColumn = c + facing.dC

    if (nextRow !in 0..<input.size) return false
    if (nextColumn !in 0..<input.first().length) return false

    val nextValue = input[nextRow][nextColumn]

    return plotValue == nextValue && f(PlotCoordinate(nextRow, nextColumn))
}

private fun PlotCoordinate.move(facing: Facing): PlotCoordinate =
    PlotCoordinate(r + facing.dR, c + facing.dC)




private fun isRegionBorder(input: List<String>, coordinate: PlotCoordinate): Boolean {
    val value = input[coordinate.r][coordinate.c]
    val adjacentRegionPlots = straight2DDirections
        .map { (dR, dC) -> PlotCoordinate(coordinate.r + dR, coordinate.c + dC) }
        .filter { (r, c) -> r in 0..<input.size && c in 0..<input.first().length }
        .filter { (r, c) -> input[r][c] == value }
    return adjacentRegionPlots.size < 4
}

private fun DirectionDelta.isOpposite(other: DirectionDelta): Boolean {
    return when {
        first == 0 && other.first == 0 -> second == other.second * -1
        second == 0 && other.second == 0 -> first == other.first * -1
        else -> false
    }
}

private typealias DirectionDelta = Pair<Int, Int>

private data class PlotCoordinate(val r: Int, val c: Int)
