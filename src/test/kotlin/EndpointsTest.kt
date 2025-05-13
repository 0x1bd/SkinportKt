import io.github.kvxd.skinportkt.SkinportAPIClient
import kotlin.test.Test

class EndpointsTest {

    private val client = SkinportAPIClient()

    @Test
    fun testItems() {
        assert(client.requestItems().size > 500)
    }

}