package org.yplin.project.utils;

import org.yplin.project.data.dto.SignInDto;
import org.yplin.project.data.dto.UserDto;

public class UserDtoGenerator {

    public static final String SIGNUP_RESPONSE_JSON = """
            {
                "data": {
                    "access_token": "fakeToken",
                    "access_expired": 1000,
                    "user": {
                        "id": 1,
                        "name": "fakeName",
                        "email": "fake@gmail.com",
                        "user_image_url": "fakeImageUrl"
                    }
                }
            }
            """;


    public static SignInDto getMockUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("fakeName");
        userDto.setEmail("fake@gmail.com");
        userDto.setUserImageUrl("fakeImageUrl");

        SignInDto signInDto = new SignInDto();
        signInDto.setAccessToken("fakeToken");
        signInDto.setAccessExpired(1000L);
        signInDto.setUser(userDto);

        return signInDto;
    }
}
