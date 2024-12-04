import kotlin.math.abs

fun main() = day(1) {
    part1 { input: List<String> ->
        expectSample(11)
        input.fold(listOf(emptyList<Long>(), emptyList<Long>())) { acc, line ->
            val lineNumbers = line.split("\\s+".toRegex()).map { it.toLong() }
            listOf(
                acc.first() + lineNumbers.first(),
                acc.last() + lineNumbers.last(),
            )
        }
            .map { it.sorted() }
            .reduce { left, right ->
                left.zip(right) { a, b ->
                    abs(a - b)
                }
            }
            .sum()
    }

    part2 { input ->
        expectSample(31)
        input.fold(listOf(emptyList<Long>(), emptyList<Long>())) { acc, line ->
            val lineNumbers = line.split("\\s+".toRegex()).map { it.toLong() }
            listOf(
                acc.first() + lineNumbers.first(),
                acc.last() + lineNumbers.last(),
            )
        }
            .let { it.first() to it.last().groupBy { it }.mapValues { it.value.size } }
            .let { (numbers, countMap) ->
                numbers.map { it * (countMap[it] ?: 0) }.sum().toLong()
            }
    }
}