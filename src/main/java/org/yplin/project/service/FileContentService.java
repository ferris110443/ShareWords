package org.yplin.project.service;

import org.yplin.project.data.form.ImageDataForm;
import org.yplin.project.data.form.MarkdownForm;

public interface FileContentService {

    void saveFileContent(MarkdownForm markdownForm);

    String saveImageContent(ImageDataForm imageDataForm);

//    MarkdownDTO getFileContent(int fileId);
}
