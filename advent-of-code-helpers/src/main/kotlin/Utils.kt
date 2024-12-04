
fun List<String>.eachLineAsListOfLongs(): List<List<Long>> =
    map { it.split("\\s+".toRegex()).map { it.toLong() } }


/**
 * Ensures given value is within range. If value is within the range, returns the value; otherwise
 * returns the min or max depending on which direction the value exceeds the range.
 *
 * @param value The value to clamp.
 * @param range The range to ensure the value falls under.
 *
 * @return A value guaranteed to fall within the provided range.
 */
fun clamp(value: Long, range: LongRange): Long =
    if (value < range.min()) range.min()
    else if (value > range.max()) range.max()
    else value

fun clamp(value: Int, range: IntRange): Int =
    if (value < range.min()) range.min()
    else if (value > range.max()) range.max()
    else value

val all2DDirections = listOf(
    -1 to -1,
    0 to -1,
    1 to -1,
    -1 to 0,
    1 to 0,
    -1 to 1,
    0 to 1,
    1 to 1,
)

val diagonal2DDirections = all2DDirections
    .filter { (dY, dX) -> dY != 0 && dX != 0 }

val straight2DDirections = all2DDirections
    .filter { (dY, dX) -> dY == 0 || dX == 0 }