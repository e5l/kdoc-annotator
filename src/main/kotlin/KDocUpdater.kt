import java.nio.file.Path

class KDocUpdater {
    private val kdocs = mutableListOf<KDocLocation>()

    fun collectForUpdate(startOffset: Int, endOffset: Int, fqname: String) {
        kdocs += KDocLocation(startOffset, endOffset, fqname)
    }

    fun executeUpdate(file: Path, link: String) {
        val content = file.toFile().readText()
        kdocs.sortBy { it.startOffset }

        val newContent = StringBuilder()
        var lastUsedIndex = 0
        for ((start, end, fqname) in kdocs) {
            newContent.append(content.substring(lastUsedIndex, start))
            lastUsedIndex = end

            val kdoc = content.substring(start, end)
            val newKdoc = updateKDocWithLink(kdoc, link, fqname)
            newContent.append(newKdoc)
        }

        newContent.append(content.substring(lastUsedIndex))
        file.toFile().writeText(newContent.toString())
    }
}

private val KDOC_TAGS = listOf(
    "param",
    "property",
    "return",
    "constructor",
    "receiver",
    "throws",
    "exception",
    "see",
    "author",
    "since",
    "suppress",
    "sample"
)

internal fun updateKDocWithLink(content: String, link: String, fqname: String): String {
    val lines = content.split("\n")
    if (lines.size == 1) {
        return buildSingleLineKDoc(lines.first(), link, fqname)
    }

    var insertionIndex = lines.indexOfFirst { line -> KDOC_TAGS.any { tag -> line.contains("@$tag") } }
    if (insertionIndex == -1) {
        insertionIndex = lines.size - 1
    }

    val indent = lines[1].takeWhile { it == ' ' }
    val blankLine = "$indent*"

    return buildString {
        for (i in 0 until insertionIndex) {
            appendLine(lines[i])
        }

        appendLine(blankLine)
        appendLine(indent + formatLink(link, fqname))

        if (insertionIndex != lines.size - 1) {
            appendLine(blankLine)
        }

        for (i in insertionIndex until lines.size - 1) {
            appendLine(lines[i])
        }

        append(lines.last())
    }
}

private fun formatLink(link: String, fqname: String): String {
    return "* [Report a problem]($link?fqname=$fqname)"
}

private fun buildSingleLineKDoc(line: String, link: String, fqname: String): String {
    val indent = line.takeWhile { it.isWhitespace() }
    val rawKDoc = line.substringAfter("/**").substringBeforeLast("*/").trim()

    return buildString {
        appendLine("$indent/**")
        appendLine("$indent * $rawKDoc")
        appendLine("$indent *")
        appendLine("$indent ${formatLink(link, fqname)}")
        append("$indent */")
    }
}