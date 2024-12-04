fun main() = day(4) {
    part1 { input ->
        expectSample(18)
        var count = 0L
        for ((r, line) in input.withIndex()) {
            for ((c, char) in line.withIndex()) {
                count += countWordInstancesAtLocation(wordToSearch, r, c, input)
            }
        }
        count
    }

    part2 { input ->
        expectSample(9)
        var count = 0L
        for ((r, line) in input.withIndex()) {
            for ((c, char) in line.withIndex()) {
                count += countXmas(r, c, input)
            }
        }
        count
    }

}

private fun countWordInstancesAtLocation(wordToSearch: String, r: Int, c: Int, input: List<String>, directionality: Pair<Int, Int>? = null): Int {
    if (r !in 0..<input.size) return 0
    if (c !in 0..<input.first().length) return 0
    if (wordToSearch.isEmpty() || input[r][c] != wordToSearch.first()) return 0
    if (wordToSearch.length == 1 && input[r][c] == wordToSearch.first()) return 1
    return (directionality?.let { listOf(it) } ?: all2DDirections)
        .map { (x, y) -> countWordInstancesAtLocation(wordToSearch.substring(1), r + y, c + x, input, x to y) }
        .sum()
}

// M.S    M.M    S.M      S.S
// .A.    .A.    .A.      .A.
// M.S    S.S    S.M      M.M

private fun countXmas(r: Int, c: Int, input: List<String>): Int {
    if (r !in 1..<(input.size - 1)) return 0
    if (c !in 1..<(input.first().length - 1)) return 0
    if (input[r][c] != 'A') return 0
    val topLeft = input[r - 1][c - 1]
    val topRight = input[r - 1][c + 1]
    val bottomLeft = input[r + 1][c - 1]
    val bottomRight = input[r + 1][c + 1]
    return when {
        topLeft == 'M' && bottomLeft == 'M' && topRight == 'S' && bottomRight == 'S' -> 1
        topLeft == 'M' && bottomLeft == 'S' && topRight == 'M' && bottomRight == 'S' -> 1
        topLeft == 'S' && bottomLeft == 'S' && topRight == 'M' && bottomRight == 'M' -> 1
        topLeft == 'S' && bottomLeft == 'M' && topRight == 'S' && bottomRight == 'M' -> 1
        else -> 0
    }
}

private val wordToSearch = "XMAS"