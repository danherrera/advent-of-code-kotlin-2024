
fun main() = day(2) {
    part1 { input ->
        expectSample(2)
        input
            .eachLineAsListOfLongs()
            .filter { isSafe(it) }
            .count()
            .toLong()
    }

    part2 { input ->
        expectSample(4)
        input.eachLineAsListOfLongs()
            .filter { levels ->
                levels.mapIndexed { i, level ->
                    if (i == levels.size) levels.subList(0, levels.size - 1)
                    else levels.subList(0, i) + levels.subList(i + 1, levels.size)
                }
                    .any { isSafe(it) }
            }
            .count()
            .toLong()
    }
}

private fun isSafe(levels: List<Long>): Boolean {
    val diffs = levels.zipWithNext { a, b -> a - b }
    return when {
        diffs.first() > 0 -> diffs.all { it in 1..3 }
        diffs.first() < 0 -> diffs.all { it in -3..-1 }
        else -> false
    }
}
