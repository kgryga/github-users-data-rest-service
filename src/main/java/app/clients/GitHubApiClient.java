package app.clients;

import app.models.dtos.GitHubUserDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class GitHubApiClient {

    private static final String GET_USER_DATA_URL = "/users/{login}";

    private final WebClient webClient;

    public Mono<GitHubUserDataDto> getUserData(String login) {
        return webClient.get()
                .uri(GET_USER_DATA_URL, login)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

}
