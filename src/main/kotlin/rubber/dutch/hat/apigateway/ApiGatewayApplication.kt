package rubber.dutch.hat.apigateway

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import rubber.dutch.hat.apigateway.config.HeaderConfig
import rubber.dutch.hat.apigateway.config.UserServiceConfig

@SpringBootApplication
@EnableConfigurationProperties(UserServiceConfig::class, HeaderConfig::class)
class ApiGatewayApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(ApiGatewayApplication::class.java, *args)
        }
    }
}
