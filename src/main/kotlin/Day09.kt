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
            .filterNot { (it as? DiskSection.Space)?.length?.let { it == 0 } == true }
            .toMutableList()

        var i = 0
        while (i < uncompactedBlocks.size) {
            val numberIndex = uncompactedBlocks.dropLast(i)
                .indexOfLast { (it as? DiskSection.Number)?.moved?.let { it == false } == true }

            val number = uncompactedBlocks[numberIndex] as DiskSection.Number

            val firstEligibleSpaceIndex =
                uncompactedBlocks.indexOfFirst { (it as? DiskSection.Space)?.let(number::fitsIn) == true }
            if (firstEligibleSpaceIndex == -1) {
                // no available space to place number
                i++
                continue
            }
            val firstEligibleSpace = uncompactedBlocks[firstEligibleSpaceIndex] as DiskSection.Space

            if (firstEligibleSpaceIndex > numberIndex) {
                // space available is after this number's location
                i++
                continue
            }

            val spaceReplacement = firstEligibleSpace.addNumber(number)
            uncompactedBlocks.removeAt(firstEligibleSpaceIndex)
            uncompactedBlocks.addAll(firstEligibleSpaceIndex, spaceReplacement)

            val nIndex = if (spaceReplacement.size == 2) {
                numberIndex + 1
            } else {
                numberIndex
            }

            uncompactedBlocks.removeAt(nIndex)
            uncompactedBlocks.add(nIndex, number.asSpace())

            i++
        }

        uncompactedBlocks
            .map { it.asString() }
            .joinToString("")
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
            check(number.length <= length) { "Number doesn't fit space" }
            val movedNumber = number.copy(moved = true)
            if (number.length == length) return listOf(movedNumber)
            return listOf(movedNumber, Space(length - number.length))
        }

        override fun asString(): String = generateSequence { SPACE }.take(length).joinToString("")
    }

    data class Number(val id: Int, val count: Int, val moved: Boolean = false) : DiskSection {
        override val length: Int = asString().length
        override fun asString(): String = generateSequence { id.toString() }.take(count).joinToString("")
        fun fitsIn(space: DiskSection.Space): Boolean = length <= space.length
        fun asSpace(): DiskSection.Space = Space(length)
    }
}