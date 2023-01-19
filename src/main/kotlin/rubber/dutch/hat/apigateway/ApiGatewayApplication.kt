package rubber.dutch.hat.apigateway

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@ConfigurationPropertiesScan("rubber.dutch.hat.apigateway.config")
@Suppress("UtilityClassWithPublicConstructor")
@EnableScheduling
class ApiGatewayApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            @Suppress("SpreadOperator")
            SpringApplication.run(ApiGatewayApplication::class.java, *args)
        }
    }
}
