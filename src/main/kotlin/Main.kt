import kotlin.io.path.Path

/**
 * KDoc annotator is a tool to add a feedback link for the KDoc documentation if this link is not present yet.
 * The link will be updated if it's necessary.
 *
 * Usage:
 * ```shell
 * kdoc-annotator /path/to/project "https://feedback-link.com"
 * ```
 *
 * Every KDoc for the public API will be updated with a link to the feedback form,
 * marked with the fully qualified name `fqname` query parameter:
 * ```kotlin
 * /**
 * * // origin body of KDoc
 *
 * * [Send Feedback about this KDoc](https://feedback-link.com?fqname=full.qualified.name)
 * **/
 */
fun main(args: Array<String>) {
    if (args.size != 2) {
        printHelp()
        return
    }

    val projectSources = args[0]
    val link = args[1]

    forEachKtFileInDirectory(Path(projectSources)) { ktFile, path ->
        annotatePublicApiKDocs(ktFile, path, link)
    }
}


fun printHelp() {
    println("KDoc annotator is a tool to add a feedback link for the KDoc documentation if this link is not present yet.")
    println("Usage:")
    println("kdoc-annotator <path-to-project sources> <link>")
}
