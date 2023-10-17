package app.controllers;

import app.clients.GitHubApiClient;
import app.models.dtos.GitHubUserDataDto;
import app.models.dtos.UserDataDto;
import app.models.entities.ApiRequest;
import app.repositories.ApiRequestsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class GetUserDataControllerTest {

    private static final String URL = "/users/{login}";
    private static final String LOGIN = "testLogin123";
    private static final long PREVIOUS_REQUEST_COUNT = 3;
    private static final long CURRENT_REQUEST_COUNT = 4;
    private static final long FIRST_REQUEST_COUNT = 1;
    private static final long PUBLIC_REPOS = 2;
    private static final long FOLLOWERS = 3;
    private static final String CALCULATIONS = "8.0";
    private static final String ID = "11";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ApiRequestsRepository apiRequestsRepository;

    @MockBean
    private GitHubApiClient gitHubApiClient;

    @AfterEach
    public void tearDown() {
        apiRequestsRepository.deleteAll().block();
    }

    @Test
    public void shouldReturnOkWithUserData() {
        // given
        apiRequestsRepository.save(new ApiRequest(LOGIN, PREVIOUS_REQUEST_COUNT)).block();

        GitHubUserDataDto gitHubUserDataDto = GitHubUserDataDto.builder()
                .id(ID)
                .followers(FOLLOWERS)
                .publicRepos(PUBLIC_REPOS)
                .login(LOGIN)
                .build();
        when(gitHubApiClient.getUserData(LOGIN)).thenReturn(Mono.just(gitHubUserDataDto));

        UserDataDto expectedResult = UserDataDto.builder().id(ID).login(LOGIN).calculations(CALCULATIONS)
                .build();

        // when
        WebTestClient.ResponseSpec response = webTestClient.get().uri(URL, LOGIN).exchange();

        // then
        response.expectStatus().is2xxSuccessful()
                .expectBody(UserDataDto.class)
                .isEqualTo(expectedResult);

        verify(gitHubApiClient).getUserData(LOGIN);
        verifyNoMoreInteractions(gitHubApiClient);

        StepVerifier.create(apiRequestsRepository.findById(LOGIN))
                .expectNext(new ApiRequest(LOGIN, CURRENT_REQUEST_COUNT))
                .verifyComplete();
    }

    @Test
    public void shouldReturnOkWithUserDataInCaseOfFirstRequestForGivenLogin() {
        // given
        GitHubUserDataDto gitHubUserDataDto = GitHubUserDataDto.builder()
                .id(ID)
                .followers(FOLLOWERS)
                .publicRepos(PUBLIC_REPOS)
                .login(LOGIN)
                .build();
        when(gitHubApiClient.getUserData(LOGIN)).thenReturn(Mono.just(gitHubUserDataDto));

        UserDataDto expectedResult = UserDataDto.builder().id(ID).login(LOGIN).calculations(CALCULATIONS)
                .build();

        // when
        WebTestClient.ResponseSpec response = webTestClient.get().uri(URL, LOGIN).exchange();

        // then
        response.expectStatus().is2xxSuccessful()
                .expectBody(UserDataDto.class)
                .isEqualTo(expectedResult);

        verify(gitHubApiClient).getUserData(LOGIN);
        verifyNoMoreInteractions(gitHubApiClient);

        StepVerifier.create(apiRequestsRepository.findById(LOGIN))
                .expectNext(new ApiRequest(LOGIN, FIRST_REQUEST_COUNT))
                .verifyComplete();
    }

    @Test
    public void shouldReturnInternalServerErrorWhenExceptionOccursDuringGettingUserDataFromGitHubApi() {
        // given
        apiRequestsRepository.save(new ApiRequest(LOGIN, PREVIOUS_REQUEST_COUNT)).block();
        when(gitHubApiClient.getUserData(LOGIN)).thenReturn(Mono.error(new RuntimeException("test")));

        // when
        WebTestClient.ResponseSpec response = webTestClient.get().uri(URL, LOGIN).exchange();

        // then
        response.expectStatus().is5xxServerError();

        verify(gitHubApiClient).getUserData(LOGIN);
        verifyNoMoreInteractions(gitHubApiClient);

        StepVerifier.create(apiRequestsRepository.findById(LOGIN))
                .expectNext(new ApiRequest(LOGIN, CURRENT_REQUEST_COUNT))
                .verifyComplete();
    }

}
