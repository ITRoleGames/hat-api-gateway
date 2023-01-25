package rubber.dutch.hat.apigateway.config

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import rubber.dutch.hat.apigateway.model.event.Event
import rubber.dutch.hat.apigateway.service.EventUnicastService

@Component
class RabbitMqConsumer(private val eventUnicastService: EventUnicastService) {

//    fun createMessageListenerContainer(queueName: String): MessageListenerContainer {
//        val mlc = SimpleMessageListenerContainer(connectionFactory);
//        mlc.addQueueNames(queueName)
//        return mlc
//    }

    @RabbitListener(queues = ["game-event"])
    fun listenMessages(message: String) {
        eventUnicastService.onNext(Event(1, message, 1))
//        val mlc = createMessageListenerContainer("game-event")
//        mlc.setupMessageListener { messageListener ->
//
//            messageListener.body
//        }
    }
}
