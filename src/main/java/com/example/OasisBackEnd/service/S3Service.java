package com.example.OasisBackEnd.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

@Service
public class S3Service {

    @Autowired
    private AmazonS3 s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = "product-images/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        logger.info("Uploading file with name: " + fileName);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        s3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);
        //s3Client.setObjectAcl(bucketName, fileName, CannedAccessControlList.PublicRead);
        String fileUrl = s3Client.getUrl(bucketName, fileName).toString();
        logger.info("File uploaded to URL: " + fileUrl);
        return fileUrl;
    }
}
