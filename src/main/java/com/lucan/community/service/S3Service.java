package com.lucan.community.service;

import com.lucan.community.message.MessageCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;
    private final String cloudFrontDomain;

    public S3Service(
            S3Client s3Client,
            @Value("${cloud.aws.s3.bucket}") String bucketName,
            @Value("${cloud.aws.cloudfront.domain}") String cloudFrontDomain
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.cloudFrontDomain = cloudFrontDomain;
    }

    public String uploadImage(MultipartFile file, String directory) {

        validateImage(file);

        String objectKey = createObjectKey(directory, file.getOriginalFilename());

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return cloudFrontDomain + "/" + objectKey;

        } catch (IOException | S3Exception e) {
            throw new IllegalStateException(MessageCode.IMAGE_UPLOAD_FAILED.getMessage(), e);
        }
    }

    public void deleteImage(String imageUrl) {

        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        try {
            URI uri = URI.create(imageUrl);

            String path = uri.getPath();

            if (path == null || path.isBlank() || path.equals("/")) {
                throw new IllegalArgumentException(MessageCode.IMAGE_DELETE_FAILED.getMessage());
            }

            String objectKey = path.startsWith("/") ? path.substring(1) : path;

            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3Client.deleteObject(request);

        } catch (IllegalArgumentException | S3Exception e) {
            throw new IllegalArgumentException(MessageCode.IMAGE_DELETE_FAILED.getMessage(), e);
        }
    }

    /**
     * 파일이 존재하고 이미지인지 검사한다.
     */
    private void validateImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(MessageCode.IMAGE_FILE_EMPTY.getMessage());
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {

            throw new IllegalArgumentException(MessageCode.INVALID_IMAGE_FILE.getMessage());
        }
    }

    /**
     * UUID를 이용해 중복되지 않는 Object Key 생성
     */
    private String createObjectKey(String directory, String originalFilename) {

        String extension = extractExtension(originalFilename);

        return directory
                + "/"
                + UUID.randomUUID()
                + extension;
    }

    /**
     * 파일 확장자 추출
     */
    private String extractExtension(String originalFilename) {

        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }

        return originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
    }
}