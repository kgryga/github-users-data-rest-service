package app.controllers;

import app.models.dtos.UserDataDto;
import app.services.UserDataRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
public class GetUserDataController {

    private final UserDataRetrievalService userDataRetrievalService;

    @GetMapping("/users/{login}")
    public Mono<ResponseEntity<UserDataDto>> getUserData(@PathVariable String login) {
        return userDataRetrievalService.getUserDataBumpingApiRequestCount(login)
                .map(ResponseEntity::ok);
    }

}
