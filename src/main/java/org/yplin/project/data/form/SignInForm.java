package org.yplin.project.data.form;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignInForm {

    @JsonProperty("email")
    private String email;

    @Nullable
    @JsonProperty("password")
    private String password;

    @Nullable
    @JsonProperty("access_token")
    private String accessToken;

}
