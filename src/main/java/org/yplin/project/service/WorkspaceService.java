package org.yplin.project.service;

import org.yplin.project.data.form.CreateWorkspaceForm;
import org.yplin.project.data.form.UpdateWorkspaceForm;
import org.yplin.project.error.NotWorkspaceOwnerException;
import org.yplin.project.model.WorkspaceModel;

public interface WorkspaceService {

    long createWorkspace(CreateWorkspaceForm createWorkspaceForm, String creatorEmail);

    void deleteWorkspace(long roomNumber, String userEmail) throws IllegalArgumentException, NotWorkspaceOwnerException;

    WorkspaceModel getWorkspaceInformation(long roomNumber);

    void updateWorkspaceInformation(UpdateWorkspaceForm updateWorkspaceForm);
}
