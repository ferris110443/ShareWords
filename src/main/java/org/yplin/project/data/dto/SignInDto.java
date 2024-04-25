package org.yplin.project.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yplin.project.model.UserModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignInDto {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("access_expired")
    private Long accessExpired;

    @JsonProperty("user")
    private UserDto user;

    public static SignInDto from(UserModel user) {
        SignInDto signInDto = new SignInDto();
        signInDto.setAccessToken(user.getAccessToken());
        signInDto.setAccessExpired(user.getAccessExpired());
        UserDto userDto = UserDto.from(user);
        signInDto.setUser(userDto);
        return signInDto;
    }


}
