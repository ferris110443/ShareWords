package org.yplin.project.service;

import org.yplin.project.data.form.CreateFileForm;
import org.yplin.project.data.form.ImageDataForm;
import org.yplin.project.data.form.MarkdownForm;

public interface FileContentService {

    void updateFileContent(MarkdownForm markdownForm);

    void createFileContent(CreateFileForm createFileForm);

    String saveImageContent(ImageDataForm imageDataForm);

    long queryWorkspaceIdFromWorkspaceName(String workspaceName);

//    MarkdownDTO getFileContent(int fileId);
}
