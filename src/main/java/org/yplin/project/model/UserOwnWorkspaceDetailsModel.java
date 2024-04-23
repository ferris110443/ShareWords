package org.yplin.project.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserOwnWorkspaceDetailsModel {


    @Id
    @Column(name = "workspace_id")
    @JsonProperty("workspace_id")
    private Long workspaceId;
    
    @Column(name = "user_id")
    @JsonProperty("user_id")
    private Long userId;


    @Column(name = "workspace_name")
    @JsonProperty("workspace_name")
    private String workspaceName;

    @Column(name = "workspace_description")
    @JsonProperty("workspace_description")
    private String workspaceDescription;

    @Column(name = "email")
    @JsonProperty("email")
    private String email;
}
