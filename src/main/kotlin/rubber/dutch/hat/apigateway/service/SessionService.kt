package rubber.dutch.hat.apigateway.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import rubber.dutch.hat.apigateway.config.HeaderConfig
import rubber.dutch.hat.apigateway.model.TokenDTO

/**
 * Сервис проверки сессии.
 */
@Service
class SessionService(
    private val headerConfig: HeaderConfig,
    webClientBuilder: WebClient.Builder,
    @Value("\${hat.services.user-service.uri}")
    private var userServiceUri: String,
    @Value("\${hat.services.user-service.tokenPath}")
    private var userServiceTokenPath: String
) {

    companion object {
        /**
         * Длина префикса "Bearer ", после которого в заголовке запроса следует токен.
         */
        const val BEARER_PREFIX_LENGTH = 7
    }

    private val webClient: WebClient

    init {
        webClient = webClientBuilder
            .baseUrl(userServiceUri)
            .build()
    }

    /**
     * Проверяет токен во входящем запросе обращаясь к сервису пользователей и если токен невалидный выбрасывает
     * исключение.
     *
     * @throws IllegalStateException Если токен отсутствует или просрочен.
     * @throws org.springframework.web.reactive.function.client.WebClientResponseException Если получен 4xx ответ.
     */
    fun validate(exchange: ServerWebExchange): Mono<ServerWebExchange> {
        val accessToken: String? = getTokenFromHeader(exchange)

        if (accessToken.isNullOrBlank()) {
            return Mono.error { error("Token not found") }
        }

        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("$userServiceTokenPath/{token}").build(accessToken)
            }
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus({ httpStatus -> httpStatus.is4xxClientError }, ClientResponse::createException)
            .bodyToMono(TokenDTO::class.java)
            .map { tokenData -> addSessionHeader(exchange, tokenData) }
    }

    private fun getTokenFromHeader(exchange: ServerWebExchange): String? {
        return exchange.request.headers.getFirst(headerConfig.authorization)?.substring(BEARER_PREFIX_LENGTH)
    }

    private fun addSessionHeader(exchange: ServerWebExchange, tokenData: TokenDTO): ServerWebExchange {
        if (tokenData.expired) {
            error("Token expired")
        }

        val mutatedRequest = exchange.request.mutate().header(headerConfig.userId, tokenData.userId.toString()).build()

        return exchange.mutate().request(mutatedRequest).build()
    }
}
