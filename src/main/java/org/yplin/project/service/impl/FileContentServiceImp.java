package org.yplin.project.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yplin.project.data.form.CreateFileForm;
import org.yplin.project.data.form.ImageDataForm;
import org.yplin.project.data.form.MarkdownForm;
import org.yplin.project.model.FileContentModel;
import org.yplin.project.repository.FileContentBatchInsertRepository;
import org.yplin.project.repository.FileContentRepository;
import org.yplin.project.repository.WorkspaceRepository;
import org.yplin.project.repository.user.UserRepository;
import org.yplin.project.service.FileContentService;
import org.yplin.project.service.WorkspaceFileContentProjection;

import java.io.IOException;
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
    @Autowired
    UserRepository userRepository;


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
//        System.out.println(workspaceId);
        fileContentModel.setWorkspaceId(workspaceId);
        fileContentModel.setFileTitle(createFileForm.getFileName());
        fileContentModel.setContent("");
        fileContentModel.setFileURL("http://localhost:8080/markdownfiles/" + createFileForm.getFileId());
        fileContentModel.setFileId(createFileForm.getFileId());
        fileContentModel.setFileDescription(createFileForm.getFileDescription());

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
//            System.out.println(imageDataBase64);
            if (imageDataBase64 != null) {
                byte[] decodedBytes = Base64.getDecoder().decode(imageDataBase64);
                String filename = UUID.randomUUID() + ".png";
                Path destinationPath = Paths.get("/home/ubuntu/sharewords/ShareWords/src/main/resources/static/userPicture/static/images");
                if (!Files.exists(destinationPath)) {
                    Files.createDirectories(destinationPath);
                }
                Path destinationFile = destinationPath.resolve(filename);
                // resolve() method returns a path that is this path with given path appended to it.
                Files.write(destinationFile, decodedBytes);
                logger.info("Image uploaded successfully");

                String imageURL = scheme + "://" + domain + "/images/" + filename;
//                System.out.println(imageURL);
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

    @Override
    public List<FileContentModel> getFileContentsByWorkspaceName(String workspaceName) {
        return fileContentRepository.findByWorkspaceId(workspaceRepository.findIdByWorkspaceName(workspaceName));
    }

    @Override
    public void deleteFileInWorkspace(String fileId) {
        logger.info("Deleting file with id: " + fileId);
        fileContentRepository.deleteByFileId(fileId);
    }

    @Override
    public String saveUserImage(MultipartFile file, long userId) throws IOException {
        final String DIRECTORY = "/home/ubuntu/sharewords/ShareWords/src/main/resources/static/userPicture/";
        String prefix = String.valueOf(userId);

        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot save empty file.");
        }

        Path directoryPath = Paths.get(DIRECTORY);
        logger.info(String.valueOf(directoryPath));
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
            logger.error("can't not find the directory");
        }

        String originalFileName = file.getOriginalFilename();
        String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));

        logger.info("originalFileName: " + originalFileName);
        logger.info("Base name: " + baseName);
        logger.info("Extension: " + extension);

        String fileName = prefix + "_" + baseName + extension;
        String fileURL = scheme + "://" + domain + "/userPicture/" + fileName;
        userRepository.updateUserImageURL(fileURL, userId);
        logger.info("fileURL : " + fileURL);


        Path targetLocation = directoryPath.resolve(fileName);
        logger.info("Target location: " + targetLocation);

        // Save the file
        file.transferTo(targetLocation.toFile());

        // Return the path or URL to access the file
        return targetLocation.toString();
    }

}
