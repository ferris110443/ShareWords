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
public class UserOwnWorkspaceDetailsDTO {
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("workspace_id")
    private Long workspaceId;
    @JsonProperty("workspace_name")
    private String workspaceName;
    @JsonProperty("workspace_description")
    private String workspaceDescription;
    @JsonProperty("email")
    private String email;
}
