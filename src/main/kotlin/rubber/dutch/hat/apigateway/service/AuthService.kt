package rubber.dutch.hat.apigateway.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import rubber.dutch.hat.apigateway.model.TokenDTO

/**
 * Сервис проверки аутентификации.
 */
@Service
class AuthService(
    webClientBuilder: WebClient.Builder,
    @Value("\${hat.services.user-service.uri}")
    private var userServiceUri: String,
    @Value("\${hat.services.user-service.tokenPath}")
    private var userServiceTokenPath: String
) {

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
    fun validate(accessToken: String?): Mono<TokenDTO> {
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
    }
}
