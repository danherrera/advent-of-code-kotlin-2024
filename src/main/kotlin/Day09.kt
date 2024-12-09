import java.math.BigInteger

fun main() = day(9) {
    part1 { input ->
        expectSample(1928)

        val uncompactedBlocks = input.first()
            .flatMapIndexed { i, char ->
                val id = i / 2
                val count = char.toString().toInt()
                when {
                    i % 2 == 0 -> generateSequence { id.toString() }.take(count).toList()
                    else -> generateSequence { '.' }.take(count).toList()
                }
            }
            .toMutableList()

        var leftPointer = 0
        var rightPointer = uncompactedBlocks.size - 1

        while (leftPointer < rightPointer) {
            if (uncompactedBlocks[leftPointer] == SPACE) {
                while (uncompactedBlocks[rightPointer] == SPACE) {
                    rightPointer--
                }
                uncompactedBlocks[leftPointer] = uncompactedBlocks[rightPointer]
                uncompactedBlocks[rightPointer] = SPACE
            }
            leftPointer++
        }
        if (leftPointer > rightPointer && uncompactedBlocks[leftPointer] == SPACE && uncompactedBlocks[rightPointer] == SPACE && uncompactedBlocks[leftPointer - 1] != SPACE) {
            leftPointer--
            uncompactedBlocks[rightPointer] = uncompactedBlocks[leftPointer]
            uncompactedBlocks[leftPointer] = SPACE
        }

        uncompactedBlocks
            .mapIndexed { i, char ->
                when (char) {
                    SPACE -> BigInteger.ZERO
                    else -> {
                        BigInteger.valueOf((i * char.toString().toInt()).toLong())
                    }
                }
            }
            .reduce { a, b -> a.add(b) }
            .toLong()
    }

    part2 { input ->
        expectSample(2858)

        val uncompactedBlocks = input.first()
            .mapIndexed { i, char ->
                val id = i / 2
                val count = char.toString().toInt()
                when {
                    i % 2 == 0 -> DiskSection.Number(id, count)
                    else -> DiskSection.Space(count)
                }
            }
            .toMutableList()

        var leftIndex = 0
        while (leftIndex < uncompactedBlocks.size) {
            while ((leftIndex < uncompactedBlocks.size - 1) && uncompactedBlocks[leftIndex] !is DiskSection.Space) {
                leftIndex++
            }
            val space = uncompactedBlocks[leftIndex] as? DiskSection.Space
            if (space == null) {
                // no more spaces to fill
                break
            }

            var rightIndex = uncompactedBlocks.size - 1
            while (uncompactedBlocks[rightIndex] !is DiskSection.Number ||
                (!(uncompactedBlocks[rightIndex] as DiskSection.Number).fitsIn(space) &&
                        rightIndex > leftIndex)
            ) {
                rightIndex--
            }

            if (rightIndex > leftIndex) {
                val number = uncompactedBlocks[rightIndex] as DiskSection.Number
                uncompactedBlocks.removeAt(rightIndex)
                uncompactedBlocks.add(rightIndex, number.asSpace())
                uncompactedBlocks.removeAt(leftIndex)
                uncompactedBlocks.addAll(leftIndex, space.addNumber(number))
            }

            leftIndex++
        }

        uncompactedBlocks
            .map { it.asString() }
            .joinToString("")
//            .also { println(it) }
            .mapIndexed { i, char ->
                when (char) {
                    SPACE -> BigInteger.ZERO
                    else -> {
                        BigInteger.valueOf((i * char.toString().toInt()).toLong())
                    }
                }
            }
            .reduce { a, b -> a.add(b) }
            .toLong()
    }
}

private const val SPACE = '.'

private sealed interface DiskSection {
    val length: Int
    fun asString(): String

    data class Space(override val length: Int) : DiskSection {
        fun addNumber(number: DiskSection.Number): List<DiskSection> {
            check (number.length <= length) { "Number doesn't fit space" }
            if (number.length == length) return listOf(number)
            return listOf(number, Space(length - number.length))
        }

        override fun asString(): String = generateSequence { SPACE }.take(length).joinToString("")
    }
    data class Number(val id: Int, val count: Int) : DiskSection {
        override val length: Int = asString().length
        override fun asString(): String = generateSequence { id.toString() }.take(count).joinToString("")
        fun fitsIn(space: DiskSection.Space): Boolean = length <= space.length
        fun asSpace(): DiskSection.Space = Space(length)
    }
}