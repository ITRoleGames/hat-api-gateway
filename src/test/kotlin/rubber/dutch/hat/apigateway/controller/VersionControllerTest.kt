package rubber.dutch.hat.apigateway.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.util.TestSocketUtils
import rubber.dutch.hat.apigateway.BaseTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
internal class VersionControllerTest : BaseTest() {

    @BeforeEach
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun `WHEN get version THAN success`() {
        client.get().uri("version")
            .exchange()
            .expectStatus().isOk
    }

    companion object {
        private var rsocketPort: Int = TestSocketUtils.findAvailableTcpPort()

        @DynamicPropertySource
        @JvmStatic
        fun initProperties(registry: DynamicPropertyRegistry) {
            registry.add("application.version") { "1.0.0" }
            registry.add("spring.rsocket.server.port") { "$rsocketPort" }
        }
    }
}
