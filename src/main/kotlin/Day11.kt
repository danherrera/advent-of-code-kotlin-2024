fun main() = day(11) {
    part1 { input ->
        expectSample(55312)

        var stones = input.first().split(" ")
            .map { Stone(it.toLong()) }

        repeat(25) {
            stones = stones.flatMap { blink(it) }
        }

        stones.size.toLong()
    }

    part2 { input ->
        var stones = input.first().split(" ")
            .map { Stone(it.toLong()) }

        val levels = 75

        val memo = (0..<levels)
            .map { Level(it) to mutableMapOf<Stone, StoneCount>() }
            .toMap()

        stones.sumOf { countStones(it, memo, Level(levels)) }
    }
}

@JvmInline
value class Stone(val value: Long)

@JvmInline
value class Level(val value: Int) {
    fun plusOne(): Level = Level(value + 1)
}

@JvmInline
value class StoneCount(val value: Long)

private fun blink(stone: Stone): List<Stone> {
    return when {
        stone.value == 0L -> listOf(Stone(1))
        stone.value.toString().length % 2 == 0 -> {
            val stringValue = stone.value.toString()
            val midpoint = stringValue.length / 2
            listOf(Stone(stringValue.take(midpoint).toLong()), Stone(stringValue.takeLast(midpoint).toLong()))
        }

        else -> listOf(Stone(stone.value * 2024))
    }
}

private fun inOrderTraversalCount(stone: Stone, destinationLevel: Int): Long {
    val count = mutableListOf<Long>(0)
    inOrderTraversalRecursive(stone, 0, destinationLevel, count)
    return count.first()
}

private fun inOrderTraversalRecursive(stone: Stone, level: Int, destinationLevel: Int, count: MutableList<Long>) {
    if (level == destinationLevel - 1) {
        val existingCount = count.first()
        count.clear()
        count.add(blink(stone).size + existingCount)
        return
    }

    for (childStone in blink(stone)) {
        inOrderTraversalRecursive(childStone, level + 1, destinationLevel, count)
    }
}

private fun multiBlink(stone: Stone, numberOfBlinks: Int): List<Stone> {
    var stones = listOf(stone)

    repeat(numberOfBlinks) {
        stones = stones.flatMap { blink(it) }
    }

    return stones
}

private fun multiBlinkCount(stone: Stone, numberOfBlinks: Int): Int = multiBlink(stone, numberOfBlinks).size

private fun countStones(
    stone: Stone,
    memo: Map<Level, MutableMap<Stone, StoneCount>>,
    maxLevel: Level,
    level: Level = Level(0),
): Long {
    if (level.value == maxLevel.value - 1) return blink(stone).size.toLong()
    val levelMapping = memo[level]!!
    if (levelMapping[stone] == null) {
        return blink(stone)
            .sumOf { countStones(it, memo, maxLevel, level.plusOne()) }
            .also { levelMapping[stone] = StoneCount(it) }
    }
    return levelMapping[stone]!!.value
}

/**
 * 0: 125 17
 * 1: 253000 1 7
 * 2: 253 0 2024 14168
 * 3: 512072 1 20 24 28676032
 * 4: 512 72 2024 2 0 2 4 2867 6032
 */
// l=0 i=0
// l=1 i=0

//private fun multiBlinkTreeCount(stone: Stone, numberOfBlinks: Int, index: Int = 0, level: Int = 0): Int {
//    val blinkedStones = blink(stone)
//    if (level == numberOfBlinks - 2) return blinkedStones
//    return multiBlinkTreeCount(blinkedStones.first(), numberOfBlinks, level = level + 1)
//
//    var count = 0
//
//    for (stone in stones) {
//        return multiBlinkTreeCount()
//    }
//
//    return count
//}