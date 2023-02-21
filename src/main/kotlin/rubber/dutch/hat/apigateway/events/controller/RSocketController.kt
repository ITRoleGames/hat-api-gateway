package rubber.dutch.hat.apigateway.events.controller

import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import rubber.dutch.hat.apigateway.events.GameEventMulticastService
import rubber.dutch.hat.apigateway.events.model.GameUpdatedEvent

@Controller
class RSocketController(
    val gameEventMulticastService: GameEventMulticastService
) {

    @ConnectMapping("/games/{gameId}/user/{userId}")
    fun onConnect(
        @DestinationVariable gameId: String,
        @DestinationVariable userId: String
    ) {
        gameEventMulticastService.onSubscribe(gameId, userId)
    }

    @MessageMapping("/games/{gameId}/user/{userId}")
    fun responseStream(
        @DestinationVariable gameId: String,
        @DestinationVariable userId: String
    ): Flux<GameUpdatedEvent> {
        return gameEventMulticastService.get(gameId, userId)
            .doOnCancel { gameEventMulticastService.onUnsubscribe(gameId, userId) }
            .doOnComplete { gameEventMulticastService.onUnsubscribe(gameId, userId) }
    }
}
