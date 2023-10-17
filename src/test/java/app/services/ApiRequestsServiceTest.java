package app.services;

import app.models.dtos.ApiRequestDto;
import app.models.entities.ApiRequest;
import app.repositories.ApiRequestsRepository;
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
public class ApiRequestsServiceTest {

    private static final String LOGIN = "testLogin123";
    private static final long PREVIOUS_REQUEST_COUNT = 2;
    private static final long CURRENT_REQUEST_COUNT = 3;

    @Mock
    private ApiRequestsRepository apiRequestsRepository;

    private ApiRequestsService apiRequestsService;

    @BeforeEach
    public void setUp() {
        apiRequestsService = new ApiRequestsService(apiRequestsRepository);
    }

    @Test
    public void shouldReturnSavedBumpedApiRequestCountForGivenLogin() {
        // given
        when(apiRequestsRepository.findById(LOGIN)).thenReturn(Mono.just(new ApiRequest(LOGIN, PREVIOUS_REQUEST_COUNT)));
        when(apiRequestsRepository.save(new ApiRequest(LOGIN, CURRENT_REQUEST_COUNT)))
                .thenReturn(Mono.just(new ApiRequest(LOGIN, CURRENT_REQUEST_COUNT)));

        // when
        Mono<ApiRequestDto> result = apiRequestsService.bumpApiRequestCount(LOGIN);

        // then
        StepVerifier.create(result).expectNext(new ApiRequestDto(LOGIN, CURRENT_REQUEST_COUNT)).verifyComplete();

        verify(apiRequestsRepository).findById(LOGIN);
        verify(apiRequestsRepository).save(new ApiRequest(LOGIN, CURRENT_REQUEST_COUNT));
        verifyNoMoreInteractions(apiRequestsRepository);
    }

    @Test
    public void shouldThrowWhenExceptionOccursDuringSavingBumpedApiRequestCountForGivenLogin() {
        // given
        when(apiRequestsRepository.findById(LOGIN)).thenReturn(Mono.just(new ApiRequest(LOGIN, PREVIOUS_REQUEST_COUNT)));
        when(apiRequestsRepository.save(new ApiRequest(LOGIN, CURRENT_REQUEST_COUNT)))
                .thenReturn(Mono.error(new RuntimeException("test")));

        // when
        Mono<ApiRequestDto> result = apiRequestsService.bumpApiRequestCount(LOGIN);

        // then
        StepVerifier.create(result).verifyError(RuntimeException.class);

        verify(apiRequestsRepository).findById(LOGIN);
        verify(apiRequestsRepository).save(new ApiRequest(LOGIN, CURRENT_REQUEST_COUNT));
        verifyNoMoreInteractions(apiRequestsRepository);
    }

    @Test
    public void shouldThrowWhenExceptionOccursDuringFindingApiRequestDataForGivenLogin() {
        // given
        when(apiRequestsRepository.findById(LOGIN)).thenReturn(Mono.error(new RuntimeException("test")));

        // when
        Mono<ApiRequestDto> result = apiRequestsService.bumpApiRequestCount(LOGIN);

        // then
        StepVerifier.create(result).verifyError(RuntimeException.class);

        verify(apiRequestsRepository).findById(LOGIN);
        verifyNoMoreInteractions(apiRequestsRepository);
    }

}
