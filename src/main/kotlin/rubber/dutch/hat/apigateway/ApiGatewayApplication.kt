package rubber.dutch.hat.apigateway

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ApiGatewayApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(ApiGatewayApplication::class.java, *args)
        }
    }
}
