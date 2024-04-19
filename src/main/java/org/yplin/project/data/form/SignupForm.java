package org.yplin.project.data.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupForm {

    @JsonProperty("username")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

}
