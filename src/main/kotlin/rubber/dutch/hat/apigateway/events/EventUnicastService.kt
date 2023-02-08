package rubber.dutch.hat.apigateway.events

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import rubber.dutch.hat.apigateway.events.model.GameUpdatedEvent

@Service
class EventUnicastService {

    private val events: Sinks.Many<GameUpdatedEvent> = Sinks.many().replay().limit(10)

    fun onNext(event: GameUpdatedEvent) {
        events.tryEmitNext(event)
    }

    fun get(): Flux<GameUpdatedEvent> {
        return events.asFlux()
    }
}
