package org.yplin.project.data.form;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkspaceCheckValidationForm {
    

    private String workspaceName;
}
