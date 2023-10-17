package app.clients;


import app.models.dtos.GitHubUserDataDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@SpringBootTest
@DirtiesContext
public class GitHubApiClientTest {

    private static final String URL_TEMPLATE = "/users/%s";
    private static final String LOGIN = "testLogin123";

    @Autowired
    private GitHubApiClient gitHubApiClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(9999));
        wireMockServer.start();
    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void shouldReturnGitHubUserData() throws JsonProcessingException {
        // given
        String url = String.format(URL_TEMPLATE, LOGIN);
        GitHubUserDataDto userDataDto = GitHubUserDataDto.builder().login(LOGIN).id("123").build();
        wireMockServer.stubFor(get(urlEqualTo(url))
                                 .willReturn(aResponse()
                                                     .withStatus(HttpStatus.OK.value())
                                                     .withHeader(ContentTypeHeader.KEY,
                                                                 MediaType.APPLICATION_JSON_VALUE)
                                                     .withBody(objectMapper.writeValueAsString(userDataDto))));

        // when
        Mono<GitHubUserDataDto> result = gitHubApiClient.getUserData(LOGIN);

        // then
        StepVerifier.create(result).expectNext(userDataDto).verifyComplete();

        wireMockServer.verify(getRequestedFor(urlEqualTo(url)));
    }

    @Test
    public void shouldThrowWhenGitHubClientReturnsErrorStatusCode() {
        // given
        String url = String.format(URL_TEMPLATE, LOGIN);
        wireMockServer.stubFor(get(urlEqualTo(url))
                                 .willReturn(aResponse()
                                                     .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));

        // when
        Mono<GitHubUserDataDto> result = gitHubApiClient.getUserData(LOGIN);

        // then
        StepVerifier.create(result).verifyError(WebClientResponseException.class);

        wireMockServer.verify(getRequestedFor(urlEqualTo(url)));
    }

}
