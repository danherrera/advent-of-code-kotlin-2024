fun main() = day(5) {
    part1 { input ->
        expectSample(143)
        val rules = input.subList(0, input.indexOf(""))
            .map {
                val first = it.substringBefore('|').toLong()
                val second = it.substringAfter('|').toLong()
                Rule(first, second)
            }

        val updates = input.subList(input.indexOf("") + 1, input.size)
            .map { it.split(",").map { it.toLong() } }

        updates
            .filterNot { it.breaksAny(rules) }
            .map { it[it.size / 2] }
            .sum()
    }

    part2 { input ->
        expectSample(123)
        val rules = input.subList(0, input.indexOf(""))
            .map {
                val first = it.substringBefore('|').toLong()
                val second = it.substringAfter('|').toLong()
                Rule(first, second)
            }

        val updates = input.subList(input.indexOf("") + 1, input.size)
            .map { it.split(",").map { it.toLong() } }

        updates
            .filter { it.breaksAny(rules) }
            .mapIndexed { i, incorrectlyOrderedList ->
                incorrectlyOrderedList.sortedWith { a, b ->
                    when {
                        rules.contains(Rule(a, b)) -> 1
                        rules.contains(Rule(b, a)) -> -1
                        else -> 0
                    }
                }
            }
            .map { it[it.size / 2] }
            .sum()
    }
}

data class Rule(val first: Long, val second: Long)

private fun List<Long>.breaks(rule: Rule): Boolean = when {
    contains(rule.first) && contains(rule.second) -> indexOf(rule.first) > indexOf(rule.second)
    else -> false
}

private fun List<Long>.breaksAny(rules: List<Rule>): Boolean = rules.filter { breaks(it) }.isNotEmpty()