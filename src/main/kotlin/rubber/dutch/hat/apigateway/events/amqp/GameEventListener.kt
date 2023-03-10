package rubber.dutch.hat.apigateway.events.amqp

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import rubber.dutch.hat.apigateway.events.GameEventMulticastService
import rubber.dutch.hat.apigateway.events.amqp.AmqpConfig.Companion.GAME_EVENT_QUEUE_NAME
import rubber.dutch.hat.game.api.GameUpdatedEvent

@Service
class GameEventListener(val gameEventMulticastService: GameEventMulticastService) {

    @RabbitListener(queues = [GAME_EVENT_QUEUE_NAME])
    fun onUserRegistration(event: GameUpdatedEvent) {
        gameEventMulticastService.onNext(event)
    }
}
