package org.yplin.project.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yplin.project.data.form.ImageDataForm;
import org.yplin.project.data.form.MarkdownForm;
import org.yplin.project.model.FileContentModel;
import org.yplin.project.repository.FileContentRepository;
import org.yplin.project.service.FileContentService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
public class FileContentServiceImp implements FileContentService {

    public static final Logger logger = LoggerFactory.getLogger(FileContentServiceImp.class);
    @Autowired
    FileContentRepository fileContentRepository;
    @Value("${project.domain}")
    private String domain;
    @Value("${project.port}")
    private int port;
    @Value("${project.scheme}")
    private String scheme;

    @Override
    public void saveFileContent(MarkdownForm markdownText) {
        FileContentModel fileContentModel = new FileContentModel();
        fileContentModel.setWorkspaceId(1);
        fileContentModel.setFileTitle("test");
        fileContentModel.setContent(markdownText.getMarkdownText());
        fileContentModel.setFileURL("http://localhost:8080/api/1.0/markdown/getMarkdownText");
        fileContentRepository.save(fileContentModel);
    }

    @Override
    public String saveImageContent(ImageDataForm imageDataForm) {
        try {
            String imageDataBase64 = imageDataForm.getImage().split(",")[1];
            System.out.println(imageDataBase64);
            if (imageDataBase64 != null) {
                byte[] decodedBytes = Base64.getDecoder().decode(imageDataBase64);
                String filename = UUID.randomUUID() + ".png";
                Path destinationPath = Paths.get("C:\\Users\\USER\\OneDrive\\Programming\\JavaProject\\AppWorks\\Personal project\\yplin\\project\\src\\main\\resources\\static\\images");
                if (!Files.exists(destinationPath)) {
                    Files.createDirectories(destinationPath);
                }
                Path destinationFile = destinationPath.resolve(filename);
                // resolve() method returns a path that is this path with given path appended to it.
                Files.write(destinationFile, decodedBytes);
                logger.info("Image uploaded successfully");

                String imageURL = scheme + "://" + domain + ":" + port + "/images/" + filename;
                System.out.println(imageURL);
                return imageURL;

            } else {
                logger.error("Error in uploading image");
                return null;

            }
        } catch (Exception e) {
            logger.error("Error in uploading image", e);
            return null;
        }
    }


}
