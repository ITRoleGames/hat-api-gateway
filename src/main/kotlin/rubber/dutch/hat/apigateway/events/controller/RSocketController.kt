package rubber.dutch.hat.apigateway.events.controller

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import rubber.dutch.hat.apigateway.events.EventUnicastService
import rubber.dutch.hat.apigateway.events.model.GameUpdatedEvent

@Controller
class RSocketController(val eventUnicastService: EventUnicastService) {

    @MessageMapping("event.stream")
    fun responseStream(): Flux<GameUpdatedEvent> {
        return eventUnicastService.get()
    }
}
