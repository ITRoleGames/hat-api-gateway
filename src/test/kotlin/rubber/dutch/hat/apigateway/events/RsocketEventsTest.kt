package rubber.dutch.hat.apigateway.events

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.rsocket.server.LocalRSocketServerPort
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.util.TestSocketUtils
import org.springframework.util.MimeTypeUtils
import org.testcontainers.shaded.org.awaitility.Awaitility
import reactor.core.Disposable
import rubber.dutch.hat.apigateway.BaseContainersTest
import rubber.dutch.hat.apigateway.events.amqp.AmqpConfig
import rubber.dutch.hat.apigateway.events.model.GameEventType
import rubber.dutch.hat.apigateway.events.model.GameUpdatedEvent
import java.net.URI
import java.util.*
import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RsocketEventsTest : BaseContainersTest() {

    @Autowired
    lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    lateinit var rsocketStrategies: RSocketStrategies

    @LocalRSocketServerPort
    var rsocketPort: Int? = null

    companion object {
        private var rsocketPort: Int = TestSocketUtils.findAvailableTcpPort()

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.rsocket.server.port") { "$rsocketPort" }
        }
    }

    @Test
    fun `WHEN GameUpdateEvent received THEN sent to rsocket subscriber`() {
        val gameId = UUID.randomUUID()
        val userId = UUID.randomUUID().toString()

        val route = "/games/$gameId/user/$userId"

        val events = mutableListOf<GameUpdatedEvent>()
        connectAndSubscribeEvents(route, events)

        sendGameEvent(gameId)

        Awaitility.await().atMost(10, TimeUnit.SECONDS).until {
            events.size > 0
        }
        assertEquals(1, events.size)
        assertEquals(gameId, events[0].gameId)
    }

    @Test
    fun `WHEN GameUpdateEvent received THEN sent to specials rsocket subscribers`() {
        val game1 = UUID.randomUUID()
        val game2 = UUID.randomUUID()
        val game1User1 = UUID.randomUUID().toString()
        val game1User2 = UUID.randomUUID().toString()
        val game2User1 = UUID.randomUUID().toString()

        val routeGame1User1 = "/games/$game1/user/$game1User1"
        val routeGame1User2 = "/games/$game1/user/$game1User2"
        val routeGame2User1 = "/games/$game2/user/$game2User1"

        val eventsGame1User1 = mutableListOf<GameUpdatedEvent>()
        val eventsGame1User2 = mutableListOf<GameUpdatedEvent>()
        val eventsGame2User1 = mutableListOf<GameUpdatedEvent>()

        connectAndSubscribeEvents(routeGame1User1, eventsGame1User1)
        connectAndSubscribeEvents(routeGame1User2, eventsGame1User2)
        connectAndSubscribeEvents(routeGame2User1, eventsGame2User1)

        sendGameEvent(game1)

        Awaitility.await().atMost(10, TimeUnit.SECONDS).until {
            eventsGame1User1.size > 0 && eventsGame1User2.size > 0
        }
        assertEquals(1, eventsGame1User1.size)
        assertEquals(1, eventsGame1User2.size)
        assertEquals(game1, eventsGame1User1[0].gameId)
        assertEquals(game1, eventsGame1User2[0].gameId)
        assertEquals(0, eventsGame2User1.size)

        sendGameEvent(game2)

        Awaitility.await().atMost(10, TimeUnit.SECONDS).until {
            eventsGame2User1.size > 0
        }

        assertEquals(1, eventsGame1User1.size)
        assertEquals(1, eventsGame1User2.size)
        assertEquals(1, eventsGame2User1.size)
        assertEquals(game2, eventsGame2User1[0].gameId)
    }

    private fun connectAndSubscribeEvents(route: String, events: MutableList<GameUpdatedEvent>): Disposable {
        return createRequester(route).subscribeGameUpdatedEvents(route, events)
    }

    private fun createRequester(route: String): RSocketRequester {
        return RSocketRequester.builder()
            .rsocketStrategies(rsocketStrategies)
            .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
            .setupRoute(route)
            .connectWebSocket(URI("ws://localhost:$rsocketPort"))
            .block()!!
    }

    private fun RSocketRequester.subscribeGameUpdatedEvents(
        route: String,
        events: MutableList<GameUpdatedEvent>
    ): Disposable {
        return this.route(route)
            .retrieveFlux(GameUpdatedEvent::class.java)
            .subscribe {
                events.add(it)
            }
    }

    private fun sendGameEvent(gameId: UUID) {
        amqpTemplate.convertAndSend(
            AmqpConfig.GAME_EVENT_QUEUE_NAME,
            GameUpdatedEvent(gameId, GameEventType.GAME_UPDATED, UUID.randomUUID())
        )
    }
}
