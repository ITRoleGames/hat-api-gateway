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


class GetCurrentUserRouteTest : BaseRouteTest() {

    companion object {
        private var userServicePort: Int = TestSocketUtils.findAvailableTcpPort()

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("hat.services.user-service.uri") { "http://localhost:$userServicePort" }
        }
    }

    @BeforeEach
    internal override fun setUp() {
        super.setUp()
        mockServerUser = ClientAndServer.startClientAndServer(userServicePort)
        super.mockTokenCall(mockServerUser)
    }

    @Test
    fun `create game success`() {
        mockServerUser.`when`(
            HttpRequest.request()
                .withMethod(HttpMethod.GET.name())
                .withHeader("user-id", USER_ID)
                .withPath("/api/v1/user/current")
        ).respond(
            HttpResponse.response()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody("{\"test\":\"test\"}")
        )

        client.get().uri("api/v1/user/current")
            .header("authorization", "Bearer $TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody(Map::class.java)
            .consumeWith { result -> assertThat(result.responseBody).isNotEmpty() }

    }
}
