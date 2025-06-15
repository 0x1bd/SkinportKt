import kotlinx.coroutines.runBlocking
import org.kvxd.skinport.dsl.skinportClient
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

// Incomplete, transactions endpoint can't be tested safely.
@Ignore
class EndpointTest {

    private val client = skinportClient {
        fileCache()
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

}