package org.yplin.project.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yplin.project.data.dto.FileNameAndDescriptionDto;
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

import java.io.*;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileContentServiceImp implements FileContentService {

    public static final Logger logger = LoggerFactory.getLogger(FileContentServiceImp.class);
    @Autowired
    FileContentRepository fileContentRepository;

    //    final String userPictureDirectory = "/home/ubuntu/sharewords/ShareWords/src/main/resources/static/userPicture/";
//    final String upLoadImageDirectory = "/home/ubuntu/sharewords/ShareWords/src/main/resources/static/images/";
    @Autowired
    FileContentBatchInsertRepository fileContentBatchInsertRepository;
    @Autowired
    WorkspaceRepository workspaceRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private S3Uploader s3Uploader;
    @Value("${static.folder.path}")
    private String staticFolderPath;
    @Value("${project.domain}")
    private String domain;
    @Value("${project.port}")
    private int port;
    @Value("${project.scheme}")
    private String scheme;

    @Value("${cloudfront.domain}")
    private String cloudFrontDomain;

    private static File convertMultipartToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
        return file;
    }

    @Override
    public void updateFileContent(MarkdownForm markdownForm) {
        List<MarkdownForm> markdownForms = List.of(markdownForm);
        fileContentBatchInsertRepository.updateFileContentInBatch(markdownForms);
    }

    @Override
    public void createFile(CreateFileForm createFileForm) {
        FileContentModel fileContentModel = new FileContentModel();
        try {
            long workspaceId = queryWorkspaceIdFromWorkspaceName(createFileForm.getRoomId());
            fileContentModel.setWorkspaceId(workspaceId);
            fileContentModel.setFileTitle(createFileForm.getFileName());
            fileContentModel.setContent("");
            fileContentModel.setFileURL("http://localhost:8080/markdownfiles/" + createFileForm.getFileId());
            fileContentModel.setFileId(createFileForm.getFileId());
            fileContentModel.setFileDescription(createFileForm.getFileDescription());
            fileContentRepository.save(fileContentModel);
        } catch (DataIntegrityViolationException e) {
            logger.error("Failed to create file due to data integrity issue: " + e.getMessage());
            throw new DataIntegrityViolationException("Data integrity violation in createFile: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("An error occurred while creating the file: " + e.getMessage());
            throw new RuntimeException("Error in createFile: " + e.getMessage(), e);
        }
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

            if (imageDataBase64 != null) {
                byte[] decodedBytes = Base64.getDecoder().decode(imageDataBase64);
                String fileName = UUID.randomUUID() + ".png";

                String userFileS3Name = "sharewords/images/" + fileName;
                try (InputStream inputStream = new ByteArrayInputStream(decodedBytes)) {
                    s3Uploader.uploadFileToS3(inputStream, userFileS3Name, decodedBytes.length);
                }


                logger.info("Image uploaded successfully");
                String imageURL = scheme + "://" + cloudFrontDomain + "/sharewords/images/" + fileName;
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

//    @Override
//    public String saveUserImage(MultipartFile file, long userId) throws IOException {
//
//        String prefix = String.valueOf(userId);
//        System.out.println("staticFolderPath : " + staticFolderPath);
////        final String userPictureDirectory = staticFolderPath + "userPicture/";
//        final String userPictureDirectory = staticFolderPath + "userPicture\\";
//
//        if (file.isEmpty()) {
//            throw new IllegalStateException("Cannot save empty file.");
//        }
//        Path directoryPath = Paths.get(userPictureDirectory);
//        logger.info(String.valueOf(directoryPath));
//        if (!Files.exists(directoryPath)) {
//            Files.createDirectories(directoryPath);
//            logger.error("can't not find the directory");
//        }
//
//        String originalFileName = file.getOriginalFilename();
//        String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
//        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
//
//        String fileName = prefix + "_" + baseName + extension;
//        String fileURL = scheme + "://" + domain + "/userPicture/" + fileName;
//        userRepository.updateUserImageURL(fileURL, userId);
//        logger.info("fileURL : " + fileURL);
//
//
//        Path targetLocation = directoryPath.resolve(fileName);
//        logger.info("Target location: " + targetLocation);
//
//        // Save the file
//        file.transferTo(targetLocation.toFile());
//
//
//        // Return the path or URL to access the file
//        return targetLocation.toString();
//    }
//

    @Override
    public String saveUserImage(MultipartFile file, long userId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot save empty file.");
        }

        String originalFileName = file.getOriginalFilename();
        String fileName = userId + "_" + originalFileName;
        String fileURL = scheme + "://" + cloudFrontDomain + "/sharewords/userPicture/" + fileName;

        // Upload to S3 directly from MultipartFile
        String userFileS3Name = "sharewords/userPicture/" + fileName;
        try {
            s3Uploader.uploadFileToS3(file, userFileS3Name);
        } catch (IOException e) {
            logger.error("Error uploading file to S3: " + e.getMessage());
            throw new RuntimeException(e);
        }

        // Update user image URL in the repository
        userRepository.updateUserImageURL(fileURL, userId);
        logger.info("Uploaded Image URL: " + fileURL);

        return fileURL;
    }

    @Override
    public void updateFileNameAndDescription(FileNameAndDescriptionDto fileNameAndDescription) {
        String fileId = fileNameAndDescription.getFileId();
        String updatedFileName = fileNameAndDescription.getFileTitle();
        String updatedFileDescription = fileNameAndDescription.getFileDescription();
        fileContentRepository.updateFileNameAndDescription(fileId, updatedFileName, updatedFileDescription);

    }


}
