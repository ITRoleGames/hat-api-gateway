package rubber.dutch.hat.apigateway

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Testcontainers

@Suppress("UtilityClassWithPublicConstructor")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class BaseContainersTest {

    companion object {
        @JvmStatic
        private val rabbitMQContainer =
            RabbitMQContainer("rabbitmq:3.9.20-management-alpine").apply { start() }

        @DynamicPropertySource
        @JvmStatic
        fun initProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.driverClassName") { "org.testcontainers.jdbc.ContainerDatabaseDriver" }
            registry.add("spring.rabbitmq.host") { rabbitMQContainer.host }
            registry.add("spring.rabbitmq.port") { rabbitMQContainer.amqpPort }
            registry.add("spring.rabbitmq.username") { rabbitMQContainer.adminUsername }
            registry.add("spring.rabbitmq.password") { rabbitMQContainer.adminPassword }
            registry.add("spring.rabbitmq.vhost") { "/" }
        }
    }
}
