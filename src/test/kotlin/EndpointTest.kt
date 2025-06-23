import kotlinx.coroutines.runBlocking
import org.kvxd.skinport.dsl.skinportClient
import org.kvxd.skinport.internalapi.InternalSkinportAPI
import org.kvxd.skinport.internalapi.internalSkinportClient
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

// Incomplete, transactions endpoint can't be tested safely.
class EndpointTest {

    private val client = skinportClient {
        fileCache()
    }

    @OptIn(InternalSkinportAPI::class)
    private val internalClient = internalSkinportClient {

    }

    @Test
    fun testItems() = runBlocking {
        assertTrue {
            client.items().isNotEmpty()
        }
    }

    @Test
    fun testSalesHistory() = runBlocking {
        assertTrue {
            client.salesHistory().isNotEmpty()
        }
    }

    @Test
    fun testOutOfStock() = runBlocking {
        assertTrue {
            client.outOfStockItems().isNotEmpty()
        }
    }

    @Test
    fun testTransactions() {
        runBlocking {
            assertFailsWith<IllegalArgumentException> {
                client.transactions()
            }
        }
    }

    @Test
    fun testInternalAPI() {
        runBlocking {
            assertTrue {
                internalClient.browse(
                    item = "AWP",
                    skin = "Printstream",

                    maxWear = 0.05f
                )
                    .items.isNotEmpty()
            }
        }
    }

}