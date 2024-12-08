fun main() = day(8) {
    part1 { input ->
        expectSample(14)

        val worldMap = WorldMap(width = input.first().length, height = input.size)

        val frequenciesToCoordinates = mutableMapOf<Char, List<AntennaCoordinate>>()

        input.forEachIndexed { row, line ->
            line.forEachIndexed { column, char ->
                if (char != EMPTY_TILE) {
                    frequenciesToCoordinates[char] = frequenciesToCoordinates[char]?.let {
                        it + AntennaCoordinate(row, column)
                    } ?: listOf(AntennaCoordinate(row, column))
                }
            }
        }

        val antinodes = mutableSetOf<AntennaCoordinate>()

        val comparedCoordinates = mutableSetOf<Pair<AntennaCoordinate, AntennaCoordinate>>()
        for ((_, coordinates) in frequenciesToCoordinates) {
            for (coordinate in coordinates) {
                for (otherCoordinate in coordinates) {
                    if (coordinate == otherCoordinate) continue
                    if (comparedCoordinates.contains(coordinate to otherCoordinate) ||
                        comparedCoordinates.contains(otherCoordinate to coordinate)) continue

                    val antennaAntinodes = worldMap.antinodes(coordinate, otherCoordinate)
                    antinodes.addAll(antennaAntinodes)
                }
            }
        }

        antinodes
            .count()
            .toLong()
    }

    part2 { input ->
        expectSample(34)

        val worldMap = WorldMap(width = input.first().length, height = input.size)

        val frequenciesToCoordinates = mutableMapOf<Char, List<AntennaCoordinate>>()

        input.forEachIndexed { row, line ->
            line.forEachIndexed { column, char ->
                if (char != EMPTY_TILE) {
                    frequenciesToCoordinates[char] = frequenciesToCoordinates[char]?.let {
                        it + AntennaCoordinate(row, column)
                    } ?: listOf(AntennaCoordinate(row, column))
                }
            }
        }

        val antinodes = mutableSetOf<AntennaCoordinate>()

        val comparedCoordinates = mutableSetOf<Pair<AntennaCoordinate, AntennaCoordinate>>()
        for ((_, coordinates) in frequenciesToCoordinates) {
            for (coordinate in coordinates) {
                for (otherCoordinate in coordinates) {
                    if (coordinate == otherCoordinate) continue
                    if (comparedCoordinates.contains(coordinate to otherCoordinate) ||
                        comparedCoordinates.contains(otherCoordinate to coordinate)) continue

                    val antennaAntinodes = worldMap.antinodesPart2(coordinate, otherCoordinate)
                    antinodes.addAll(antennaAntinodes)
                }
            }
        }

        antinodes
            .count()
            .toLong()
    }
}

private const val EMPTY_TILE = '.'

private data class WorldMap(val width: Int, val height: Int) {
    val validRowRange = 0..<height
    val validColumnRange = 0..<height
}

private data class AntennaCoordinate(val row: Int, val column: Int)

@Suppress("Unused") // for debugging
private fun WorldMap.printAntinodes(antinodes: Set<AntennaCoordinate>) {
    for (r in validRowRange) {
        for (c in validColumnRange) {
            print(when (AntennaCoordinate(r, c) in antinodes) {
                true -> '#'
                else -> EMPTY_TILE
            })
        }
        print("\n")
    }
}

private fun WorldMap.antinodes(a: AntennaCoordinate, b: AntennaCoordinate): List<AntennaCoordinate> {
    val xDiff = a.column - b.column
    val yDiff = a.row - b.row

    val antinodeAX = a.column + xDiff
    val antinodeAY = a.row + yDiff

    val antinodeBX = b.column - xDiff
    val antinodeBY = b.row - yDiff

    return listOf(
        AntennaCoordinate(antinodeAY, antinodeAX),
        AntennaCoordinate(antinodeBY, antinodeBX),
    )
        .filter { (row, column) -> row in 0..<height && column in 0..<width }
}
private fun WorldMap.antinodesPart2(a: AntennaCoordinate, b: AntennaCoordinate): List<AntennaCoordinate> {
    val antinodes = mutableListOf<AntennaCoordinate>()
    val xDiff = a.column - b.column
    val yDiff = a.row - b.row

    var antinodeAX = a.column + xDiff
    var antinodeAY = a.row + yDiff

    while (antinodeAX in validColumnRange && antinodeAY in validRowRange) {
        antinodes.add(AntennaCoordinate(antinodeAY, antinodeAX))
        antinodeAX += xDiff
        antinodeAY += yDiff
    }

    var antinodeBX = b.column - xDiff
    var antinodeBY = b.row - yDiff

    while (antinodeBX in validColumnRange && antinodeBY in validRowRange) {
        antinodes.add(AntennaCoordinate(antinodeBY, antinodeBX))
        antinodeBX -= xDiff
        antinodeBY -= yDiff
    }

    return antinodes + a + b
}
