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
public class WorkspaceMemberDto {
    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String name;

    @JsonProperty("workspaceName")
    private String workspaceName;

    @JsonProperty("userId")
    private long userId;

    @JsonProperty("workspaceId")
    private long workspaceId;

    @JsonProperty("workspaceOwner")
    private String workspaceOwner;

    @JsonProperty("userImageUrl")
    private String userImageUrl;
}
