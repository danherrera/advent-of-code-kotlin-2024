import java.util.LinkedList

fun main() = day(10) {
    part1 { input ->
        expectSample(36)

        val map = input.map { line -> line.map { it.toString().toInt() } }

        var sum = 0L
        for ((r, row) in map.withIndex()) {
            for ((c, _) in row.withIndex()) {
                sum += traverseTrail(map, r, c)
            }
        }
        sum
    }

    part2 { input ->
        expectSample(81)

        val map = input.map { line -> line.map { it.toString().toInt() } }

        var sum = 0L
        for ((r, row) in map.withIndex()) {
            for ((c, _) in row.withIndex()) {
                sum += traverseTrailPart2(map, r, c)
            }
        }
        sum
    }
}

private val validDirections = listOf(
    -1 to 0, // up
    0 to 1,  // right
    1 to 0,  // down
    0 to -1, // left
)

private fun traverseTrail(map: List<List<Int>>, r: Int, c: Int): Int {
    val rows = map.size
    val columns = map.first().size
    val visited = Array(rows) { BooleanArray(columns) }
    val queue = LinkedList<Pair<Int, Int>>()

    if (map[r][c] != 0) return 0

    var sum = 0

    queue.offer(r to c)
    visited[r][c] = true

    while (queue.isNotEmpty()) {
        val (currentRow, currentColumn) = queue.poll()
        val currentValue = map[currentRow][currentColumn]

        if (currentValue == 9) {
            sum += 1
            continue
        }

        for ((dR, dC) in validDirections) {
            val nextRow = currentRow + dR
            val nextColumn = currentColumn + dC

            if (nextRow in 0..<rows && nextColumn in 0..<columns && !visited[nextRow][nextColumn] && map[nextRow][nextColumn] == (currentValue + 1)) {
                queue.offer(nextRow to nextColumn)
                visited[nextRow][nextColumn] = true
            }
        }
    }

    return sum
}

private fun traverseTrailPart2(map: List<List<Int>>, r: Int, c: Int): Int {
    val rows = map.size
    val columns = map.first().size
    val visited = Array(rows) { BooleanArray(columns) }
    val queue = LinkedList<Pair<Int, Int>>()

    if (map[r][c] != 0) return 0

    val origin = r to c
    var sum = 0

    queue.offer(r to c)
    visited[r][c] = true

    while (queue.isNotEmpty()) {
        val (currentRow, currentColumn) = queue.poll()
        val currentValue = map[currentRow][currentColumn]

        if (currentValue == 9) {
            sum += countPaths(map, origin, currentRow to currentColumn)
            continue
        }

        for ((dR, dC) in validDirections) {
            val nextRow = currentRow + dR
            val nextColumn = currentColumn + dC

            if (nextRow in 0..<rows && nextColumn in 0..<columns && !visited[nextRow][nextColumn] && map[nextRow][nextColumn] == (currentValue + 1)) {
                queue.offer(nextRow to nextColumn)
                visited[nextRow][nextColumn] = true
            }
        }
    }

    return sum
}

private fun countPaths(map: List<List<Int>>, origin: Pair<Int, Int>, destination: Pair<Int, Int>): Int {
    val rows = map.size
    val columns = map.first().size
    val visited = Array(rows) { BooleanArray(columns) }
    val queue = LinkedList<Pair<Int, Int>>()

    val (originR, originC) = origin
    val (destinationR, destinationC) = destination

    if (map[originR][originC] != 0) return 0

    var totalPaths = 0
    queue.offer(originR to originC)
    visited[originR][originC] = true

    while (queue.isNotEmpty()) {
        val (currentRow, currentColumn) = queue.poll()
        val currentValue = map[currentRow][currentColumn]

        if (currentRow == destinationR && currentColumn == destinationC) {
            totalPaths += 1
            continue
        }

        for ((dR, dC) in validDirections) {
            val nextRow = currentRow + dR
            val nextColumn = currentColumn + dC

            if (nextRow in 0..<rows && nextColumn in 0..<columns /*&& !visited[nextRow][nextColumn]*/ && map[nextRow][nextColumn] == (currentValue + 1)) {
                queue.offer(nextRow to nextColumn)
                visited[nextRow][nextColumn] = true
            }
        }
    }

    return totalPaths
}
