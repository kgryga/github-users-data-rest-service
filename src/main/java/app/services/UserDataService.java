package app.services;

import app.clients.GitHubApiClient;
import app.models.dtos.GitHubUserDataDto;
import app.models.dtos.UserDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserDataService {

    private final GitHubApiClient gitHubApiClient;

    public Mono<UserDataDto> getUserData(String login) {
        return gitHubApiClient.getUserData(login)
                .map(UserDataService::createUserDataDto);
    }

    private static UserDataDto createUserDataDto(GitHubUserDataDto gitHubUserDataDto) {
        return UserDataDto.builder()
                .id(gitHubUserDataDto.id())
                .login(gitHubUserDataDto.login())
                .name(gitHubUserDataDto.name())
                .type(gitHubUserDataDto.type())
                .avatarUrl(gitHubUserDataDto.avatarUrl())
                .createdAt(gitHubUserDataDto.createdAt())
                .calculations(getCalculations(gitHubUserDataDto))
                .build();
    }

    private static String getCalculations(GitHubUserDataDto gitHubUserDataDto) {
        return Optional.ofNullable(gitHubUserDataDto.followers())
                .flatMap(followers -> Optional.ofNullable(gitHubUserDataDto.publicRepos())
                        .map(publicRepos -> calculate(followers, publicRepos)))
                .map(String::valueOf)
                .orElse(null);
    }

    private static Float calculate(Long followers, Long publicRepos) {
        return followers == 0 ? null : (float) 6 / followers * (2 + publicRepos);
    }

}
