package rubber.dutch.hat.apigateway.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import rubber.dutch.hat.apigateway.config.HeaderConfig
import rubber.dutch.hat.apigateway.config.UserServiceConfig
import rubber.dutch.hat.apigateway.model.TokenDTO

/**
 * Сервис проверки сессии.
 */
@Service
class SessionService @Autowired constructor(
        private val userServiceConfig: UserServiceConfig,
        private val headerConfig: HeaderConfig,
        webClientBuilder: WebClient.Builder
) {

    private val webClient: WebClient

    init {
        webClient = webClientBuilder
                .baseUrl(userServiceConfig.host)
                .build()
    }

    /**
     * Проверяет токен во входящем запросе обращаясь к сервису пользователей и если токен невалидный выбрасывает
     * исключение.
     *
     * @throws IllegalStateException Если токен отсутствует или просрочен
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException Если по API лучен 4xx ответ
     */
    fun validate(exchange: ServerWebExchange): Mono<ServerWebExchange> {
        val accessToken: String? = exchange.request.headers.getFirst(headerConfig.authorization)

        if (accessToken.isNullOrBlank()) {
            return Mono.error { IllegalStateException() }
        }

        return webClient.get()
                .uri { uriBuilder ->
                    uriBuilder.path("${userServiceConfig.tokenUrlPath}/{token}").build(accessToken)
                }
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus({ httpStatus -> httpStatus.is4xxClientError }, ClientResponse::createException)
                .bodyToMono(TokenDTO::class.java)
                .map { tokenData -> addSessionHeader(exchange, tokenData) }
    }

    private fun addSessionHeader(exchange: ServerWebExchange, tokenData: TokenDTO): ServerWebExchange {

        if (tokenData.expired) {
            throw java.lang.IllegalStateException()
        }

        val mutatedRequest = exchange.request.mutate().header(headerConfig.userId, tokenData.userId.toString()).build()

        return exchange.mutate().request(mutatedRequest).build()
    }
}
