import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDeclarationContainer
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.isPublic
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import java.nio.file.Path


fun annotatePublicApiKDocs(file: KtFile, filePath: Path, link: String) {
    val updater = KDocUpdater()

    traversePublicApi(file) { declaration, fqname: String ->
        updater.updateKDoc(declaration, fqname)
    }

    updater.executeUpdate(filePath, link)
}

fun traversePublicApi(file: KtFile, block: (KtDeclaration, String) -> Unit) {
    traversePublicApi(file.declarations, name = file.packageFqName.asString(), block = block)
}

fun traversePublicApi(declarations: List<KtDeclaration>, name: String, block: (KtDeclaration, String) -> Unit) {
    if (name.isBlank()) return

    for (declaration: KtDeclaration in declarations) {
        if (!declaration.isPublic) continue
        val fqname = "$name.${declaration.name}"
        block(declaration, fqname)

        if (declaration !is KtDeclarationContainer) continue

        traversePublicApi(declaration.declarations, fqname, block)
    }
}

data class KDocLocation(val startOffset: Int, val endOffset: Int, val fqname: String)

fun KDocUpdater.updateKDoc(declaration: KtDeclaration, fqname: String) {
    val kdoc: KDoc? = declaration.docComment
    if (kdoc == null) return

    if (kdoc.text.contains(fqname)) return

    collectForUpdate(kdoc.startOffset, kdoc.endOffset, "$fqname:${declaration.lineNumber}")
}

val KtDeclaration.lineNumber: Int get() = containingFile.text.substring(0, startOffset).count { it == '\n' } + 1