package app.services;

import app.models.dtos.UserDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@RequiredArgsConstructor
@Service
public class UserDataRetrievalService {

    private final UserDataService userDataService;
    private final ApiRequestsService apiRequestsService;

    public Mono<UserDataDto> getUserDataBumpingApiRequestCount(String login) {
        return Mono.zipDelayError(apiRequestsService.bumpApiRequestCount(login), userDataService.getUserData(login))
                .map(Tuple2::getT2);
    }

}
