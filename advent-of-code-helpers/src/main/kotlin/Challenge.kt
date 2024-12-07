import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.FileNotFoundException
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.time.measureTime

class PartContext : CoroutineScope by CoroutineScope(Dispatchers.Default) {
    internal var expectedSampleAnswer: Long = 0L
        private set

    internal lateinit var part: suspend PartContext.(List<String>) -> Long

    fun expectSample(answer: Long) {
        expectedSampleAnswer = answer
    }
}

class DayContext {
    internal var part1Context: PartContext = PartContext()
    internal var part2Context: PartContext = PartContext()

    fun part1(f: suspend PartContext.(List<String>) -> Long) = runBlocking {
        part1Context.part = f
    }

    fun part2(f: suspend PartContext.(List<String>) -> Long) {
        part2Context.part = f
    }

    internal suspend fun evaluatePart1(input: List<String>): Long = with(part1Context) {
        part1Context.part(input)
    }

    internal suspend fun evaluatePart2(input: List<String>): Long = with(part2Context) {
        part2Context.part(input)
    }
}

fun day(day: Int, f: suspend DayContext.() -> Unit) = runBlocking {
    val dayString = day.toString().padStart(2, '0')
    println(
        """
        ================================
                     Day $dayString
        ================================
    """.trimIndent()
    )


    val classLoader = f.javaClass.classLoader

    val dayContext = DayContext()
    dayContext.f()

    val sampleFileName = "Day${dayString}_sample.txt"
    val sample1Result = classLoader.readFileContentAsListOfStrings(sampleFileName)
        .map { dayContext.evaluatePart1(it) }
        .onSuccess { answer ->
            check(answer == dayContext.part1Context.expectedSampleAnswer) {
                "Sample answer of '$answer' does not match expected result of '${dayContext.part1Context.expectedSampleAnswer}'"
            }
            println("Part 1 sample answer is correct!")
        }

    if (sample1Result.isFailure) {
        println("Something went wrong: ${sample1Result.exceptionOrNull()}")
        return@runBlocking
    }

    val fileName = "Day${dayString}.txt"

    val part1Duration = measureTime {
        val firstResult = classLoader.readFileContentAsListOfStrings(fileName)
            .map { dayContext.evaluatePart1(it) }
            .onSuccess { answer ->
                println("Part 1 answer is: $answer")
            }

        if (firstResult.isFailure) {
            println("Something went wrong: ${firstResult.exceptionOrNull()}")
            return@runBlocking
        }
    }
    println("Part 1 took $part1Duration")

    println("--------------------------------")

    val sample2FileName = "Day${dayString}_sample2.txt"
    val sample2Result = classLoader.readFileContentAsListOfStrings(sample2FileName)
        .fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.success(classLoader.readFileContentAsListOfStrings(sampleFileName).getOrThrow()) }
        )
        .map { dayContext.evaluatePart2(it) }
        .onSuccess { answer ->
            check(answer == dayContext.part2Context.expectedSampleAnswer) {
                "Sample answer of '$answer' does not match expected result of '${dayContext.part2Context.expectedSampleAnswer}'"
            }
            println("Part 2 sample answer is correct!")
        }

    if (sample2Result.isFailure) {
        println("Something went wrong: ${sample2Result.exceptionOrNull()}")
        return@runBlocking
    }

    val part2Duration = measureTime {
        val part2FileName = "Day${dayString}_part2.txt"
        val part2Result = classLoader.readFileContentAsListOfStrings(part2FileName)
            .fold(
                onSuccess = { Result.success(it) },
                onFailure = { Result.success(classLoader.readFileContentAsListOfStrings(fileName).getOrThrow()) },
            )
            .map { dayContext.evaluatePart2(it) }
            .onSuccess { answer ->
                println("Part 2 answer is: $answer")
            }

        if (part2Result.isFailure) {
            println("Something went wrong: ${part2Result.exceptionOrNull()}")
            return@runBlocking
        }
    }
    println("Part 2 took $part2Duration")
    println("================================")
}

fun ClassLoader.readFileContentAsListOfStrings(fileName: String): Result<List<String>> =
    getResource(fileName)?.path
        ?.let { runCatching { Path(it).readLines() } }
        ?: Result.failure(FileNotFoundException("File `$fileName` not found"))