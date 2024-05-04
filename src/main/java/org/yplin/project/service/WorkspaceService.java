package org.yplin.project.service;

import org.yplin.project.data.form.CreateWorkspaceForm;
import org.yplin.project.data.form.UpdateWorkspaceForm;
import org.yplin.project.error.NotWorkspaceOwnerException;
import org.yplin.project.model.WorkspaceModel;

public interface WorkspaceService {

    void createWorkspace(CreateWorkspaceForm createWorkspaceForm, String creatorEmail);

    void deleteWorkspace(String workspaceName, String userEmail) throws IllegalArgumentException, NotWorkspaceOwnerException;

    WorkspaceModel getWorkspaceInformation(String workspaceName);

    void updateWorkspaceInformation(UpdateWorkspaceForm updateWorkspaceForm);
}
