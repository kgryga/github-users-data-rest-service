package app.services;

import app.models.dtos.ApiRequestDto;
import app.models.dtos.UserDataDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDataRetrievalServiceTest {

    private static final String LOGIN = "testLogin321";
    private static final long REQUEST_COUNT = 3;
    private static final String ID = "5";
    private static final String NAME = "Test Login";

    @Mock
    private ApiRequestsService apiRequestsService;

    @Mock
    private UserDataService userDataService;

    private UserDataRetrievalService userDataRetrievalService;

    @BeforeEach
    public void setUp() {
        userDataRetrievalService = new UserDataRetrievalService(userDataService, apiRequestsService);
    }

    @Test
    public void shouldReturnUserDataAndBumpApiRequestCount() {
        // given
        when(apiRequestsService.bumpApiRequestCount(LOGIN))
                .thenReturn(Mono.just(new ApiRequestDto(LOGIN, REQUEST_COUNT)));
        UserDataDto userDataDto = UserDataDto.builder().id(ID).login(LOGIN).name(NAME).build();
        when(userDataService.getUserData(LOGIN)).thenReturn(Mono.just(userDataDto));

        // when
        Mono<UserDataDto> result = userDataRetrievalService.getUserDataBumpingApiRequestCount(LOGIN);

        // then
        StepVerifier.create(result).expectNext(userDataDto).verifyComplete();

        verify(apiRequestsService).bumpApiRequestCount(LOGIN);
        verify(userDataService).getUserData(LOGIN);
        verifyNoMoreInteractions(apiRequestsService, userDataService);
    }

    @Test
    public void shouldThrowWhenExceptionOccursDuringBumpingApiRequestCount() {
        // given
        when(apiRequestsService.bumpApiRequestCount(LOGIN))
                .thenReturn(Mono.error(new RuntimeException("test")));
        UserDataDto userDataDto = UserDataDto.builder().id(ID).login(LOGIN).name(NAME).build();
        when(userDataService.getUserData(LOGIN)).thenReturn(Mono.just(userDataDto));

        // when
        Mono<UserDataDto> result = userDataRetrievalService.getUserDataBumpingApiRequestCount(LOGIN);

        // then
        StepVerifier.create(result).verifyError(RuntimeException.class);

        verify(apiRequestsService).bumpApiRequestCount(LOGIN);
        verify(userDataService).getUserData(LOGIN);
        verifyNoMoreInteractions(apiRequestsService, userDataService);
    }

    @Test
    public void shouldThrowWhenExceptionOccursDuringGettingUserData() {
        // given
        when(apiRequestsService.bumpApiRequestCount(LOGIN))
                .thenReturn(Mono.just(new ApiRequestDto(LOGIN, REQUEST_COUNT)));
        when(userDataService.getUserData(LOGIN)).thenReturn(Mono.error(new RuntimeException("test")));

        // when
        Mono<UserDataDto> result = userDataRetrievalService.getUserDataBumpingApiRequestCount(LOGIN);

        // then
        StepVerifier.create(result).verifyError(RuntimeException.class);

        verify(apiRequestsService).bumpApiRequestCount(LOGIN);
        verify(userDataService).getUserData(LOGIN);
        verifyNoMoreInteractions(apiRequestsService, userDataService);
    }

}
