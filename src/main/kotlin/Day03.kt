fun main() = day(3) {
    part1 { input ->
        expectSample(161)
        input.map { line ->
            multiplicationPattern.findAll(line)
                .map {
                    numberPattern.findAll(it.value)
                        .map { it.value }
                        .run { first().toLong() to last().toLong() }
                }
                .map { (a, b) -> a * b }
                .sum()
        }.sum()
    }

    part2 { input ->
        expectSample(48)
        val inputAsSingleLine = input.joinToString("")
        var shouldCount = true
        var sum = 0L
        for (match in multiplicationPart2Pattern.findAll(inputAsSingleLine)) {
            when {
                dontPattern.matches(match.value) -> shouldCount = false
                doPattern.matches(match.value) -> shouldCount = true
                multiplicationPattern.matches(match.value) -> {
                    if (shouldCount) {
                        sum += numberPattern.findAll(match.value)
                            .map { it.value }
                            .run { first().toLong() * last().toLong() }
                    }
                }
            }
        }
        sum
    }
}

private val multiplicationPattern = "mul\\(\\d{1,3},\\d{1,3}\\)".toRegex()
private val numberPattern = "\\d{1,3}".toRegex()
private val multiplicationPart2Pattern = "mul\\(\\d{1,3},\\d{1,3}\\)|do\\(\\)|don't\\(\\)".toRegex()
private val dontPattern = "don't\\(\\).*".toRegex()
private val doPattern = "do\\(\\).*".toRegex()
