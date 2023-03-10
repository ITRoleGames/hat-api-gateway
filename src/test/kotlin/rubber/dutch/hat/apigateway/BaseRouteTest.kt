package rubber.dutch.hat.apigateway

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.util.TestSocketUtils
import rubber.dutch.hat.apigateway.model.TokenDTO
import java.util.*

class BaseRouteTest : BaseTest() {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    lateinit var mockServerUser: ClientAndServer

    lateinit var mockServerGame: ClientAndServer

    companion object {

        const val TOKEN = "1234"
        const val USER_ID = "6e36074d-0614-47d7-bd61-e3840ea5b4d8"

        private var userServicePort: Int = TestSocketUtils.findAvailableTcpPort()

        private var gameServicePort: Int = TestSocketUtils.findAvailableTcpPort()

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("application.services.user-service.uri") { "http://localhost:$userServicePort" }
            registry.add("application.services.game-service.uri") { "http://localhost:$gameServicePort" }
        }
    }

    @BeforeEach
    override fun setUp() {
        super.setUp()
        mockServerUser = ClientAndServer.startClientAndServer(userServicePort)
        mockTokenCall(mockServerUser)
        mockServerGame = ClientAndServer.startClientAndServer(gameServicePort)
    }

    @AfterEach
    internal fun tearDown() {
        mockServerUser.stop()
        mockServerGame.stop()
    }

    internal fun mockTokenCall(userServerMock: ClientAndServer) {
        userServerMock.`when`(
            HttpRequest.request()
                .withMethod(HttpMethod.GET.name())
                .withPath("/api/v1/tokens/$TOKEN")
        ).respond(
            HttpResponse.response()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody(
                    objectMapper.writeValueAsString(
                        TokenDTO(token = TOKEN, expired = false, userId = UUID.fromString(USER_ID))
                    )
                )
        )
    }
}
