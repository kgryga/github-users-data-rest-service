package app.services;

import app.clients.GitHubApiClient;
import app.models.dtos.GitHubUserDataDto;
import app.models.dtos.UserDataDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDataServiceTest {

    private static final String LOGIN = "testLogin123";
    private static final String ID = "12345";
    private static final String NAME = "Test Login";

    @Mock
    private GitHubApiClient gitHubApiClient;

    private UserDataService userDataService;

    @BeforeEach
    public void setUp() {
        userDataService = new UserDataService(gitHubApiClient);
    }

    @ParameterizedTest
    @CsvSource(value = {"1,3,30.0", "200,5,0.21", "0,3,null", "null,null,null"}, nullValues={"null"})
    public void shouldReturnUserData(Long followers, Long publicRepos, String calculations) {
        // given
        GitHubUserDataDto gitHubUserDataDto = GitHubUserDataDto.builder()
                .id(ID)
                .login(LOGIN)
                .name(NAME)
                .followers(followers)
                .publicRepos(publicRepos)
                .build();
        when(gitHubApiClient.getUserData(LOGIN)).thenReturn(Mono.just(gitHubUserDataDto));

        UserDataDto expectedResult = UserDataDto.builder().id(ID).login(LOGIN).name(NAME).calculations(calculations)
                .build();

        // when
        Mono<UserDataDto> result = userDataService.getUserData(LOGIN);

        // then
        StepVerifier.create(result).expectNext(expectedResult).verifyComplete();

        verify(gitHubApiClient).getUserData(LOGIN);
        verifyNoMoreInteractions(gitHubApiClient);
    }

    @Test
    public void shouldReturnEmptyMonoWhenNoUsersDataFound() {
        // given
        when(gitHubApiClient.getUserData(LOGIN)).thenReturn(Mono.empty());

        // when
        Mono<UserDataDto> result = userDataService.getUserData(LOGIN);

        // then
        StepVerifier.create(result).verifyComplete();

        verify(gitHubApiClient).getUserData(LOGIN);
        verifyNoMoreInteractions(gitHubApiClient);
    }

    @Test
    public void shouldThrowWhenExceptionOccursDuringGettingUserData() {
        // given
        when(gitHubApiClient.getUserData(LOGIN)).thenReturn(Mono.error(new RuntimeException("test")));

        // when
        Mono<UserDataDto> result = userDataService.getUserData(LOGIN);

        // then
        StepVerifier.create(result).verifyError(RuntimeException.class);

        verify(gitHubApiClient).getUserData(LOGIN);
        verifyNoMoreInteractions(gitHubApiClient);
    }

}
