package rubber.dutch.hat.apigateway.service

import reactor.core.publisher.Flux
import rubber.dutch.hat.apigateway.model.event.Event

interface EventUnicastService {

    fun onNext(next: Event)

    fun getMessages(): Flux<Event>
}
