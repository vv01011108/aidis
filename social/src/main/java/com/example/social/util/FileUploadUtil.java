package com.example.social.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUploadUtil {

    public static String saveFile(String uploadDir, String fileName, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        // 폴더가 존재하지 않으면 생성
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 파일 저장 경로
        Path filePath = uploadPath.resolve(fileName);
        file.transferTo(filePath.toFile());

        return filePath.toString();
    }
}