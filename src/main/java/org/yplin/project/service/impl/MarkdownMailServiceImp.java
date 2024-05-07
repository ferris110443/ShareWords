package org.yplin.project.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yplin.project.configuration.MGSendingEmailConfig;
import org.yplin.project.data.form.MailServiceForm;
import org.yplin.project.model.FileContentModel;
import org.yplin.project.service.FileContentService;
import org.yplin.project.service.MarkdownMailService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class MarkdownMailServiceImp implements MarkdownMailService {

    public static final Logger logger = LoggerFactory.getLogger(MarkdownMailServiceImp.class);

    @Autowired
    FileContentService fileContentService;
    @Autowired
    MGSendingEmailConfig mgSendingEmailConfig;
    @Autowired
    MGServiceImp mgServiceImp;
    @Value("${static.folder.path}")
    private String staticFolderPath;

    @Override
    public void sendMarkdownFiles(MailServiceForm mailServiceForm) {
        String recipientEmail = mailServiceForm.getRecipientEmail();
        String fileId = mailServiceForm.getFileId();

        FileContentModel fileContentModel = fileContentService.getFileContent(fileId);
        String fileName = sanitizeFilename(fileContentModel.getFileTitle());
        String fileContent = fileContentModel.getContent();
        Path path = Paths.get(staticFolderPath + "markdowns/" + fileName + ".md");
        String filePath = path.toAbsolutePath().toString();
        try {
            Files.write(path, fileContent.getBytes());
            System.out.println("File saved successfully: " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving the file: " + e.getMessage());
        }

        // Send the email
        mgServiceImp.sendSimpleMessage(recipientEmail, filePath, fileName);


    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
    }
}
