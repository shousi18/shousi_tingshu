package com.shousi.controller;

import com.shousi.minio.MinioUploader;
import com.shousi.result.RetVal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件上传管理")
@RestController
@RequestMapping("/api/album")
public class FileUploadController {

    @Autowired
    private MinioUploader minioUploader;

    @Operation(summary = "文件上传")
    @PostMapping("/fileUpload")
    public RetVal<String> fileUpload(MultipartFile file) throws Exception {
        String url = minioUploader.uploadFile(file);
        return RetVal.ok(url);
    }
}
