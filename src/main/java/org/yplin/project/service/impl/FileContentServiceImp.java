package org.yplin.project.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yplin.project.configuration.WebSocketConfiguration;
import org.yplin.project.data.form.MarkdownForm;
import org.yplin.project.model.FileContent;
import org.yplin.project.repository.FileContentRepository;
import org.yplin.project.service.FileContentService;

@Slf4j
@Service
public class FileContentServiceImp implements FileContentService {


    public static final Logger logger = LoggerFactory.getLogger(FileContentServiceImp.class);
    @Autowired
    FileContentRepository fileContentRepository;
    @Override
    public void saveFileContent(MarkdownForm markdownText) {
        FileContent fileContent = new FileContent();
        fileContent.setContent(markdownText.getMarkdownText());
        fileContent.setFileId(123);
        fileContent.setFileTitle(markdownText.getTitle());
        fileContent.setFileURL("http://localhost:8080/api/1.0/markdown/getMarkdownText");
        fileContentRepository.save(fileContent);
    }


}
