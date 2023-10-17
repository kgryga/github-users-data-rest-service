package app.services;

import app.models.dtos.ApiRequestDto;
import app.models.entities.ApiRequest;
import app.repositories.ApiRequestsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ApiRequestsService {

    private static final long FIRST_REQUEST = 1;

    private final ApiRequestsRepository apiRequestsRepository;

    public Mono<ApiRequestDto> bumpApiRequestCount(String login) {
        return apiRequestsRepository.findById(login)
                .flatMap(apiRequest -> saveApiRequest(apiRequest.getLogin(), incrementRequestCount(apiRequest)))
                .switchIfEmpty(Mono.defer(() -> saveApiRequest(login, FIRST_REQUEST)))
                .map(saveResult -> new ApiRequestDto(saveResult.getLogin(), saveResult.getRequestCount()));
    }

    private Mono<ApiRequest> saveApiRequest(String login, Long requestCount) {
        return apiRequestsRepository.save(new ApiRequest(login, requestCount));
    }

    private static long incrementRequestCount(ApiRequest apiRequest) {
        return apiRequest.getRequestCount() + 1;
    }

}
