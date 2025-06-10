import kotlinx.coroutines.runBlocking
import org.kvxd.skinport.SkinportException
import org.kvxd.skinport.dsl.skinportClient
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

// Incomplete, transactions endpoint can't be tested safely.
class EndpointTest {

    private val client = skinportClient { }

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
    fun testOutOfSale() = runBlocking {
        assertTrue {
            client.outOfSalesItems().isNotEmpty()
        }
    }

    @Test
    fun testTransactions() {
        runBlocking {
            assertFailsWith<SkinportException> {
                client.transactions()
            }
        }
    }

}