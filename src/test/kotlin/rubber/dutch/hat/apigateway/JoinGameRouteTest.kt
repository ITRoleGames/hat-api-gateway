package rubber.dutch.hat.apigateway

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class JoinGameRouteTest : BaseRouteTest() {

    @Test
    fun `join game`() {
        mockServerGame.`when`(
            HttpRequest.request()
                .withMethod(HttpMethod.POST.name())
                .withHeader("user-id", USER_ID)
                .withPath("/api/v1/game/join")
        )?.respond(
            HttpResponse.response()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody("{\"test\":\"test\"}")
        )

        client.post().uri("api/v1/game/join")
            .header("authorization", "Bearer $TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody(Map::class.java)
            .consumeWith { result -> assertThat(result.responseBody).isNotEmpty() }
    }

    @Test
    fun `WHEN join game without JWN token THEN Unauthorized`() {
        client.post().uri("api/v1/game/join")
            .exchange()
            .expectStatus().isUnauthorized
    }
}
