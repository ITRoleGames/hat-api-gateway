package rubber.dutch.hat.apigateway.service

import org.springframework.stereotype.Component
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Flux
import rubber.dutch.hat.apigateway.model.event.Event

@Component
class EventUnicastServiceImpl : EventUnicastService {

    val processor: EmitterProcessor<Event>

    init {
        processor = EmitterProcessor.create()
    }

    override fun onNext(next: Event) {
        processor.onNext(next)
    }

    override fun getMessages(): Flux<Event> {
        return processor.publish().autoConnect()
    }
}
