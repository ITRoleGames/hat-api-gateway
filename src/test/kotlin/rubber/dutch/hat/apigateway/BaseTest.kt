package rubber.dutch.hat.apigateway

import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseTest {

    @LocalServerPort
    private var port: Int = 0

    lateinit var client: WebTestClient

    @BeforeEach
    internal fun setUp() {
        client = WebTestClient.bindToServer().baseUrl("http://localhost:$port").build()
    }
}
