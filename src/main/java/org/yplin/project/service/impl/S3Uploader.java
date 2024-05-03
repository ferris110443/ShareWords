package org.yplin.project.service.impl;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import lombok.extern.apachecommons.CommonsLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CommonsLog
@Component
public class S3Uploader {
    public static final Logger logger = LoggerFactory.getLogger(S3Uploader.class);
    @Value("${s3.access.key}")
    private String accessKey;
    @Value("${s3.secret.key}")
    private String secretKey;
    @Value("${s3.bucket.name}")
    private String bucketName;

    private AmazonS3 getS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        // Create an S3 client
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    public void uploadFile(String saveKeyName, File file) {
        AmazonS3 s3 = getS3Client();
        try {
            s3.putObject(bucketName, saveKeyName, file);
        } catch (AmazonServiceException e) {
            log.error("upload " + saveKeyName + " failed: " + e.getMessage());
        }
    }

    void uploadFileToS3(InputStream dataStream, String keyName, long contentLength) throws IOException {
        AmazonS3 s3Client = getS3Client();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType("image/png");  // Set content type as necessary
        try {
            s3Client.putObject(new PutObjectRequest(bucketName, keyName, dataStream, metadata));
            logger.info("Successfully uploaded image to S3 with key: " + keyName);
        } catch (AmazonServiceException e) {
            logger.error("Failed to upload file to S3: " + e.getMessage());
            throw e;
        }
    }

    void uploadFileToS3(MultipartFile multipartFile, String keyName) throws IOException {
        AmazonS3 s3Client = getS3Client();
        try (InputStream inputStream = multipartFile.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());
            s3Client.putObject(new PutObjectRequest(bucketName, keyName, inputStream, metadata));
        } catch (AmazonServiceException e) {
            logger.error("Failed to upload file to S3: " + e.getMessage());
            throw e;
        }
    }


    public List<String> getFileList(String folderFilePrefix) {
        AmazonS3 s3 = getS3Client();

        ListObjectsV2Request request = new ListObjectsV2Request()
                .withPrefix(folderFilePrefix);
        List<String> keyList = new ArrayList<>();
        try {
            ListObjectsV2Result result = s3.listObjectsV2(request);
            List<S3ObjectSummary> objects = result.getObjectSummaries();

            keyList = objects.stream()
                    .map(S3ObjectSummary::getKey)
                    .collect(Collectors.toList());
        } catch (AmazonServiceException e) {
            log.error("Get file list with folder " + folderFilePrefix + " failed: " + e.getMessage());
        }
        return keyList;
    }


}
