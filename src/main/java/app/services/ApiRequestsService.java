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

    private final ApiRequestsRepository apiRequestsRepository;

    public Mono<ApiRequestDto> bumpApiRequestCount(String login) {
        return apiRequestsRepository.findById(login)
                .flatMap(apiRequest -> saveBumpedApiRequestCount(apiRequest.getLogin(), apiRequest))
                .map(saveResult -> new ApiRequestDto(saveResult.getLogin(), saveResult.getRequestCount()));
    }

    private Mono<ApiRequest> saveBumpedApiRequestCount(String login, ApiRequest apiRequest) {
        return apiRequestsRepository.save(new ApiRequest(login, incrementRequestCount(apiRequest)));
    }

    private static long incrementRequestCount(ApiRequest apiRequest) {
        return apiRequest.getRequestCount() + 1;
    }

}
