package org.yplin.project.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yplin.project.data.form.CreateFileForm;
import org.yplin.project.data.form.ImageDataForm;
import org.yplin.project.data.form.MarkdownForm;
import org.yplin.project.model.FileContentModel;
import org.yplin.project.repository.FileContentBatchInsertRepository;
import org.yplin.project.repository.FileContentRepository;
import org.yplin.project.repository.WorkspaceRepository;
import org.yplin.project.service.FileContentService;
import org.yplin.project.service.WorkspaceFileContentProjection;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileContentServiceImp implements FileContentService {

    public static final Logger logger = LoggerFactory.getLogger(FileContentServiceImp.class);
    @Autowired
    FileContentRepository fileContentRepository;

    @Autowired
    FileContentBatchInsertRepository fileContentBatchInsertRepository;

    @Autowired
    WorkspaceRepository workspaceRepository;

    @Value("${project.domain}")
    private String domain;
    @Value("${project.port}")
    private int port;
    @Value("${project.scheme}")
    private String scheme;


    @Override
    public void updateFileContent(MarkdownForm markdownForm) {
        List<MarkdownForm> markdownForms = List.of(markdownForm);
        fileContentBatchInsertRepository.updateFileContentInBatch(markdownForms);
    }


    @Override
    public void createFile(CreateFileForm createFileForm) {
        FileContentModel fileContentModel = new FileContentModel();

        long workspaceId = queryWorkspaceIdFromWorkspaceName(createFileForm.getRoomId());
        System.out.println(workspaceId);
        fileContentModel.setWorkspaceId(workspaceId);
        fileContentModel.setFileTitle(createFileForm.getFileName());
        fileContentModel.setContent("");
        fileContentModel.setFileURL("http://localhost:8080/markdownfiles/" + createFileForm.getFileId());
        fileContentModel.setFileId(createFileForm.getFileId());

        fileContentRepository.save(fileContentModel);
    }


    public long queryWorkspaceIdFromWorkspaceName(String workspaceName) {

        Long id = workspaceRepository.findIdByWorkspaceName(workspaceName);
        if (id != null) {
            return id;
        } else {
            throw new EntityNotFoundException("Workspace not found with name: " + workspaceName);
        }
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


    public List<WorkspaceFileContentProjection> getWorkspaceFilesContent(String roomId) {
        return fileContentRepository.getWorkspaceFilesContent(roomId);
    }

    @Override
    public FileContentModel getFileContent(String fileId) {
        return fileContentRepository.findByFileId(fileId);
    }

}
