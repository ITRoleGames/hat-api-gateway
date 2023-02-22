package rubber.dutch.hat.apigateway

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

class GetCurrentUserRouteTest : BaseRouteTest() {

    @Test
    fun `create game success`() {
        mockServerUser.`when`(
            HttpRequest.request()
                .withMethod(HttpMethod.GET.name())
                .withHeader("user-id", USER_ID)
                .withPath("/api/v1/user/current")
        ).respond(
            HttpResponse.response()
                .withStatusCode(HttpStatus.OK.value())
                .withContentType(org.mockserver.model.MediaType.APPLICATION_JSON)
                .withBody("{\"test\":\"test\"}")
        )

        client.get().uri("api/v1/user/current")
            .header("authorization", "Bearer $TOKEN")
            .exchange()
            .expectStatus().isOk
            .expectBody(Map::class.java)
            .consumeWith { result -> assertThat(result.responseBody).isNotEmpty() }
    }
}
