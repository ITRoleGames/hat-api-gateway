package rubber.dutch.hat.apigateway.events.amgp

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service
import rubber.dutch.hat.apigateway.events.EventUnicastService
import rubber.dutch.hat.apigateway.events.amgp.AmqpConfig.Companion.QUEUE_NAME
import rubber.dutch.hat.apigateway.events.model.GameUpdatedEvent

@Service
class GameEventListener(val eventUnicastService: EventUnicastService) {

    @RabbitListener(queues = [QUEUE_NAME])
    fun onUserRegistration(event: GameUpdatedEvent) {
        eventUnicastService.onNext(event)
    }

}
