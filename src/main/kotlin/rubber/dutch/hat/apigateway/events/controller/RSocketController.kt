package rubber.dutch.hat.apigateway.events.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import rubber.dutch.hat.apigateway.events.GameEventMulticastService
import rubber.dutch.hat.game.api.GameUpdatedEvent
import rubber.dutch.hat.apigateway.service.AuthService

@Controller
class RSocketController(
    val gameEventMulticastService: GameEventMulticastService,
    private val authService: AuthService
) {

    private val logger: Logger = LoggerFactory.getLogger(RSocketController::class.java)

    @ConnectMapping("/games/{gameId}/user/{userId}")
    fun onConnect(
        requester: RSocketRequester,
        @DestinationVariable gameId: String,
        @DestinationVariable userId: String,
        @Header(RsocketConfig.ACCESS_TOKEN_HEADER) accessToken: String,
    ) {
        authService.validate(accessToken)
            .doOnSuccess { tokenDto ->
                if (tokenDto != null && !tokenDto.expired) {
                    gameEventMulticastService.onSubscribe(gameId, userId)
                } else {
                    requester.rsocket()?.dispose()
                }
            }
            .doOnError { ex ->
                logger.error("An error occurred on token validation", ex)
                requester.rsocket()?.dispose()
            }.subscribe()
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
