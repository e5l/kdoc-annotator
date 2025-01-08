import kotlin.test.Test
import kotlin.test.assertEquals

class KdocUpdaterTest {

    @Test
    fun `update single line KDoc`() {

        val kdoc = """/** Hello world */ """
        val result = updateKDocWithLink(kdoc, "https://example.com", "a.b.c")

        assertEquals("""
            /**
             * Hello world
             *
             * [Report a problem](https://example.com?fqname=a.b.c)
             */
        """.trimIndent(), result)
    }

    @Test
    fun `update multi line KDoc`() {
        val kdoc = """
            /**
             * Hello world
             */
        """.trimIndent()
        val result = updateKDocWithLink(kdoc, "https://example.com", "a.b.c")
        assertEquals("""
            /**
             * Hello world
             *
             * [Report a problem](https://example.com?fqname=a.b.c)
             */
        """.trimIndent(), result)
    }

    @Test
    fun `update KDoc with param section`() {
        val kdoc = """
            /**
             * Hello world
             * @param a The first param
             */
        """.trimIndent()
        val result = updateKDocWithLink(kdoc, "https://example.com", "a.b.c")
        assertEquals("""
            /**
             * Hello world
             *
             * [Report a problem](https://example.com?fqname=a.b.c)
             *
             * @param a The first param
             */
        """.trimIndent(), result)
    }
}

