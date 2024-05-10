package org.yplin.project.data.form;

import lombok.Data;

@Data
public class UpdateWorkspaceForm {
    private long oldWorkspaceId;
    private String oldWorkspaceName;
    private String newWorkspaceName;
    private String newWorkspaceNameDescription;
}
