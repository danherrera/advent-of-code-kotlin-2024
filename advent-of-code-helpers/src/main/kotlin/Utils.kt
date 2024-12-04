
fun List<String>.eachLineAsListOfLongs(): List<List<Long>> =
    map { it.split("\\s+".toRegex()).map { it.toLong() } }