package com.example.finalproject.global.utils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.finalproject.global.enums.ErrorCode.NOT_FOUND_DATA;

@Component
@RequiredArgsConstructor
public class S3Utils {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    public List<String> uploadFile(List<MultipartFile> multipartFile) {
        List<String> fileUrl=new ArrayList<>();
        try {
            for (MultipartFile file : multipartFile) {
                String fileName = UUID.randomUUID().toString();
                fileUrl.add("https://" + bucket + ".s3.amazonaws.com/" + fileName);
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(file.getContentType());
                metadata.setContentLength(file.getSize());
                amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
            }
            return fileUrl;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
