package org.yplin.project.service;

public interface WorkspaceFileContentProjection {
    String getFileTitle();

    String getContent();

    String getFileURL();

    String getFileId();

    String getWorkspaceName();

    String getWorkspaceDescription();

    String getWorkspaceOwner();
}