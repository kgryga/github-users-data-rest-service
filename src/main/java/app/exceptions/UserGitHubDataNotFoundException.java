package app.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class UserGitHubDataNotFoundException extends ResponseStatusException {
    public UserGitHubDataNotFoundException(String login) {
        super(HttpStatusCode.valueOf(404), String.format("No GitHub data found for user with login %s", login));
    }
}
