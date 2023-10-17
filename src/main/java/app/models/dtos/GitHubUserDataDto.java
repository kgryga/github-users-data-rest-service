package app.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubUserDataDto(String id, String login, String name, String type, Long followers,
                                @JsonProperty(value = "public_repos") Long publicRepos,
                                @JsonProperty(value = "avatar_url") String avatarUrl,
                                @JsonProperty(value = "created_at") String createdAt) {

}
