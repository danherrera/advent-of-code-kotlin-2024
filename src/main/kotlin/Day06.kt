import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

fun main() = day(6) {
    part1 { input ->
        expectSample(41)

        val guardLocation = findGuard(input) ?: error("Guard not found")

        val world = World(input.map { it.toMutableList() })

        val visitedLocations = input
            .mapIndexed { r, row ->
                row.mapIndexed { c, column ->
                    guardLocation.row == r && guardLocation.column == c
                }.toMutableList()
            }

        traverse(guardLocation, world, visitedLocations)

        visitedLocations
            .flatMap { it }
            .count { it }
            .toLong()
    }

    part2 { input ->
        expectSample(6)

        val guardLocation = findGuard(input) ?: error("Guard not found")
        input.flatMapIndexed { r, row ->
            row.mapIndexed { c, tile ->
                async {
                    if (tile == EMPTY_TILE) {
                        val world = World(input.map { it.toMutableList() })
                        world.value[r][c] = OBSTACLE
                        val guardDirection = Direction.fromChar(world.value[guardLocation.row][guardLocation.column])
                        val guardHareLocation = guardLocation + guardDirection
                        traversePart2(guardLocation, guardDirection, world, guardHareLocation, guardDirection)
                            .also {
                                if (it) {
                                    println("> Adding obstacle at [$r][$c] introduces a cycle")
                                }
                            }
                    } else {
                        false
                    }
                }
            }
        }
            .awaitAll()
            .count { it }
            .toLong()
    }
}

private tailrec fun traverse(
    guardLocation: Coordinate,
    world: World,
    visitedLocations: List<MutableList<Boolean>>,
) {
    val guardDirection = Direction.fromChar(world.value[guardLocation.row][guardLocation.column])

    val isFacingEscape = when (guardDirection) {
        Direction.UP -> guardLocation.row == 0
        Direction.RIGHT -> guardLocation.column == world.getWidth() - 1
        Direction.DOWN -> guardLocation.row == world.getHeight() - 1
        Direction.LEFT -> guardLocation.column == 0
    }

    if (isFacingEscape) return

    val nextLocation = guardLocation + guardDirection
    val isFacingObstacle = world.value[nextLocation.row][nextLocation.column] == OBSTACLE

    if (isFacingObstacle) {
        //rotate guard
        world.value[guardLocation.row][guardLocation.column] = when (guardDirection) {
            Direction.UP -> Direction.RIGHT.char
            Direction.RIGHT -> Direction.DOWN.char
            Direction.DOWN -> Direction.LEFT.char
            Direction.LEFT -> Direction.UP.char
        }

        traverse(
            guardLocation,
            world,
            visitedLocations,
        )
        return
    }

    // move forward
    world.value[guardLocation.row][guardLocation.column] = EMPTY_TILE

    world.value[nextLocation.row][nextLocation.column] = guardDirection.char
    visitedLocations[nextLocation.row][nextLocation.column] = true
    traverse(nextLocation, world, visitedLocations)
}

private const val EMPTY_TILE = '.'
private const val OBSTACLE = '#'

private operator fun Coordinate.plus(direction: Direction): Coordinate =
    Coordinate(row + direction.y, column + direction.x)

@JvmInline
private value class World(val value: List<MutableList<Char>>) {
    fun getWidth() = value.first().size
    fun getHeight() = value.size
}

private data class Coordinate(val row: Int, val column: Int)

private fun findGuard(world: List<String>): Coordinate? {
    for ((r, row) in world.withIndex()) {
        for ((c, char) in row.withIndex()) {
            if (isGuard(char)) {
                return Coordinate(r, c)
            }
        }
    }
    return null
}

private fun isGuard(char: Char): Boolean = Direction.entries.find { it.char == char } != null

private enum class Direction(val char: Char, val x: Int, val y: Int) {
    UP('^', 0, -1),
    RIGHT('>', 1, 0),
    DOWN('v', 0, 1),
    LEFT('<', -1, 0);

    companion object {
        fun fromChar(char: Char): Direction = when (char) {
            UP.char -> UP
            RIGHT.char -> RIGHT
            DOWN.char -> DOWN
            LEFT.char -> LEFT
            else -> error("Unknown Direction")
        }
    }
}

private fun Coordinate.isOutOfBounds(world: World): Boolean =
    row !in 0..<world.getHeight() || column !in 0..<world.getWidth()

private fun World.moveStep(location: Coordinate, direction: Direction): Pair<Coordinate, Direction> {
    if (location.isOutOfBounds(this)) return location to direction

    var nextDirection = direction
    while (
        value[clamp((location + nextDirection).row, 0..<getHeight())][clamp(
            (location + nextDirection).column,
            0..<getWidth()
        )] == OBSTACLE
    ) {
        // rotate guard
        nextDirection = when (nextDirection) {
            Direction.UP -> Direction.RIGHT
            Direction.RIGHT -> Direction.DOWN
            Direction.DOWN -> Direction.LEFT
            Direction.LEFT -> Direction.UP
        }
    }

    return (location + nextDirection) to nextDirection
}


/** return true if has a cycle **/
private tailrec fun traversePart2(
    guardLocation: Coordinate,
    guardDirection: Direction,
    world: World,
    guardHareLocation: Coordinate,
    guardHareDirection: Direction,
): Boolean {
    if (guardLocation == guardHareLocation && guardDirection == guardHareDirection) return true

    val (nextGuardLocation, nextGuardDirection) = world.moveStep(guardLocation, guardDirection)

    val (nextHareLocation, nextHareDirection) = world.moveStep(guardHareLocation, guardHareDirection)
    val (nextNextHareLocation, nextNextHareDirection) = world.moveStep(nextHareLocation, nextHareDirection)

    if (nextNextHareLocation.isOutOfBounds(world)) return false

    return traversePart2(nextGuardLocation, nextGuardDirection, world, nextNextHareLocation, nextNextHareDirection)
}
