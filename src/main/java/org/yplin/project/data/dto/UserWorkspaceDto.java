package org.yplin.project.data.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserWorkspaceDto {


    @JsonProperty("roomNumber")
    private long roomNumber;

    @JsonProperty("workspaceName")
    private String workspaceName;

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("email")
    private String email;
}
