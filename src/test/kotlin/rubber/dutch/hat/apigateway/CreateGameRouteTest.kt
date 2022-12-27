package rubber.dutch.hat.apigateway

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.util.TestSocketUtils


@Suppress("UastIncorrectHttpHeaderInspection")
class CreateGameRouteTest : BaseRouteTest() {

    companion object {
        private var userServicePort: Int = TestSocketUtils.findAvailableTcpPort()

        private var gameServicePort: Int = TestSocketUtils.findAvailableTcpPort()

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("hat.services.user-service.uri") { "http://localhost:$userServicePort" }
            registry.add("hat.services.game-service.uri") { "http://localhost:$gameServicePort" }
        }
    }

    @BeforeEach
    internal override fun setUp() {
        super.setUp()
        mockServerUser = ClientAndServer.startClientAndServer(userServicePort)
        super.mockTokenCall(mockServerUser)
        mockServerGame = ClientAndServer.startClientAndServer(gameServicePort)
    }

    @Test
    fun `create game success`() {
        mockServerGame?.`when`(
            HttpRequest.request()
                .withMethod(HttpMethod.POST.name())
                .withHeader("user-id", USER_ID)
                .withPath("/api/v1/games")
        )?.respond(
            HttpResponse.response()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody("{\"test\":\"test\"}")
        )

        client.post().uri("api/v1/games")
            .header("authorization", "Bearer $TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody(Map::class.java)
            .consumeWith { result -> assertThat(result.responseBody).isNotEmpty() }

    }
}
