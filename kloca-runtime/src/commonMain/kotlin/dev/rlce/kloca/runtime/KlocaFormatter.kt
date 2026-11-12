package dev.rlce.kloca.runtime

/** Portable formatter for the placeholders accepted in Kloca translations. */
internal object KlocaFormatter {
    // The closing brace is escaped: Kotlin/JS compiles regexes in Unicode mode, which rejects a bare "}".
    private val klocaPlaceholder = Regex("\\{(\\d+)\\}")
    private val placeholder =
        Regex("%(?:(\\d+)\\$)?[-#+ 0,(<]*\\d*(?:\\.\\d+)?(?:[@bBhHsScCdoxXeEfgGaA]|[tT][A-Za-z])")

    fun format(value: String, args: Array<out Any>): String {
        var nextSequentialArgument = 0
        val escapedPercent = "\u0000KLOCA_PERCENT\u0000"
        val protectedValue = value.replace("%%", escapedPercent)

        val expandedKlocaPlaceholders = klocaPlaceholder.replace(protectedValue) { match ->
            val argumentIndex = match.groupValues[1].toIntOrNull()
            argumentIndex?.let(args::getOrNull)?.toString() ?: match.value
        }

        return placeholder.replace(expandedKlocaPlaceholders) { match ->
            val explicitIndex = match.groupValues[1].takeIf { it.isNotEmpty() }
                ?.toIntOrNull()
                ?.minus(1)
            val argumentIndex = explicitIndex ?: nextSequentialArgument++
            args.getOrNull(argumentIndex)?.toString() ?: match.value
        }.replace(escapedPercent, "%")
    }
}
