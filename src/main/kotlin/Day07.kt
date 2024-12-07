import kotlin.math.pow

fun main() = day(7) {
    part1 { input ->
        expectSample(3749)

        val equations = input
            .map { it.lineToEquation() }

        val allowedOperations = linkedSetOf(
            Operation.ADD,
            Operation.MULTIPLY,
        )

        equations.mapNotNull { equation ->
            generateCombinations(allowedOperations, equation.operators.size - 1)
                .map { equation to it }
                .takeIf { list ->
                    list.any { (equation, ops) ->
                        evaluateLeftToRight(equation.operators, ops) == equation.testValue
                    }
                }
        }
            .sumOf {
                it.first().first.testValue
            }
    }

    part2 { input ->
        expectSample(11387)

        val equations = input
            .map { it.lineToEquation() }

        val allowedOperations = linkedSetOf(
            Operation.ADD,
            Operation.MULTIPLY,
            Operation.CONCAT,
        )

        equations.mapNotNull { equation ->
            generateCombinations(allowedOperations, equation.operators.size - 1)
                .map { equation to it }
                .takeIf { list ->
                    list.any { (equation, ops) ->
                        evaluateLeftToRight(equation.operators, ops) == equation.testValue
                    }
                }
        }
            .sumOf {
                it.first().first.testValue
            }
    }
}

private fun String.lineToEquation(): Equation = Equation(
    testValue = substringBefore(':').toLong(),
    operators = substringAfter(": ").split(" ").map { it.toLong() }
)

private data class Equation(val testValue: Long, val operators: List<Long>)

enum class Operation {
    ADD, MULTIPLY, CONCAT
}

private fun generateCombinations(operations: LinkedHashSet<Operation>, combinationSize: Int): Sequence<List<Operation>> {
    val totalNumbers = operations.size.toDouble().pow((combinationSize).toDouble()).toInt()

    val maxNaryString = decimalToNary(totalNumbers, operations.size)

    return generateSequence(0) { it + 1 }
        .take(totalNumbers)
        .map { decimalToNary(it, operations.size).padStart(maxNaryString.length - 1, '0') }
        .map { naryString ->
            naryString.map { digitChar ->
                operations.elementAt(digitChar.toString().toInt())
            }
        }
}

private fun decimalToNary(decimal: Int, n: Int): String {
    val integers = mutableListOf<Int>()
    var decimalNumber = decimal

    while (decimalNumber > 0) {
        integers.add(decimalNumber % n)
        decimalNumber /= n
    }

    return integers.reversed().joinToString("")
}

private fun evaluateLeftToRight(operators: List<Long>, operations: List<Operation>): Long {
    require(operations.size + 1 == operators.size) {
        "Require an operation between each number"
    }

    val mutableOperators = operators.toMutableList()
    val mutableOperations = operations.toMutableList()

    while (mutableOperations.isNotEmpty()) {
        val operation = mutableOperations[0]
        val n1 = mutableOperators[0]
        val n2 = mutableOperators[1]
        mutableOperators[0] = when (operation) {
            Operation.ADD -> n1 + n2
            Operation.MULTIPLY -> n1 * n2
            Operation.CONCAT -> "$n1$n2".toLong()
        }
        mutableOperations.removeAt(0)
        mutableOperators.removeAt(1)
    }

    return mutableOperators.first()
}
