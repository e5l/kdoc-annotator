import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class IntegrationTest {
    @Test
    fun testLockFreeLinkedList() {

        val projectSources = "./src/test/test-data"
        val link = "https://ktor.io/feedback"


        val path = Path("../")

        forEachKtFileInDirectory(Path(projectSources)) { ktFile, path ->
            annotatePublicApiKDocs(ktFile, path, link)
        }
        
    }
}