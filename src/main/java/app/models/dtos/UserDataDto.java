package app.models.dtos;


import lombok.Builder;

@Builder(toBuilder = true)
public record UserDataDto(String id, String login, String name, String type, String avatarUrl, String createdAt,
                          String calculations) {

}
