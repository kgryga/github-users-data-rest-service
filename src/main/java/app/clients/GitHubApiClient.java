package app.clients;

import app.exceptions.UserGitHubDataNotFoundException;
import app.models.dtos.GitHubUserDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@Component
public class GitHubApiClient {

    private static final String GET_USER_DATA_URL = "/users/{login}";

    private final WebClient webClient;

    public Mono<GitHubUserDataDto> getUserData(String login) {
        return webClient.get()
                .uri(GET_USER_DATA_URL, login)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<GitHubUserDataDto>() {
                })
                .onErrorResume(WebClientResponseException.class,
                               exception -> NOT_FOUND.equals(exception.getStatusCode())
                                       ? Mono.error(new UserGitHubDataNotFoundException(login))
                                       : handleClientGatewayError());
    }

    private static Mono<GitHubUserDataDto> handleClientGatewayError() {
        return Mono.error(new ResponseStatusException(HttpStatusCode.valueOf(502),
                                                      "GitHub API Client gateway error"));
    }

}
