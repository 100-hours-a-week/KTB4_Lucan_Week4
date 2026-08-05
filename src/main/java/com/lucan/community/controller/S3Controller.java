package com.lucan.community.controller;

import com.lucan.community.dto.response.ApiResponse;
import com.lucan.community.message.MessageCode;
import com.lucan.community.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "test") String directory
    ) {
        String objectKey = s3Service.uploadImage(file, directory);

        return ResponseEntity.ok(
                new ApiResponse(
                        MessageCode.IMAGE_UPLOAD_SUCCESS.getMessage(),
                        objectKey
                )
        );
    }
}