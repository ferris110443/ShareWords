package org.yplin.project.service;

import org.yplin.project.data.form.CreateFileForm;
import org.yplin.project.data.form.ImageDataForm;
import org.yplin.project.data.form.MarkdownForm;
import org.yplin.project.model.FileContentModel;

import java.util.List;

public interface FileContentService {

    void updateFileContent(MarkdownForm markdownForm);

    void createFile(CreateFileForm createFileForm);

    String saveImageContent(ImageDataForm imageDataForm);

    long queryWorkspaceIdFromWorkspaceName(String workspaceName);

    List<WorkspaceFileContentProjection> getWorkspaceFilesContent(String roomId);


    FileContentModel getFileContent(String fileId);

    List<FileContentModel> getFileContentsByWorkspaceName(String workspaceName);
}
