package rubber.dutch.hat.apigateway

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class CreateUserRouteTest : BaseRouteTest() {

    @Test
    fun `create user success`() {
        mockServerUser.`when`(
            HttpRequest.request()
                .withMethod(HttpMethod.POST.name())
                .withPath("/api/v1/users")
        ).respond(
            HttpResponse.response()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody("{\"test\":\"test\"}")
        )

        client.post().uri("api/v1/users")
            .exchange()
            .expectStatus().isOk
            .expectBody(Map::class.java)
            .consumeWith { result -> assertThat(result.responseBody).isNotEmpty() }
    }
}
