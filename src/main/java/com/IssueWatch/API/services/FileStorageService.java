package com.IssueWatch.API.services;

import com.IssueWatch.API.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/jpg",
            "application/pdf",
            "text/plain"
    );

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }

        if (file.getContentType() == null || !ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("File type is not allowed");
        }
    }

    private String cleanFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new BadRequestException("Invalid file name");
        }

        String cleaned = Paths.get(fileName)
                .getFileName()
                .toString();

        if (cleaned.contains("..")) {
            throw new BadRequestException("Invalid file name");
        }

        return cleaned;
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex == -1) {
            return "";
        }

        return fileName.substring(lastDotIndex);
    }

    public record StoredFile (
            String originalFileName,
            String storedFileName,
            String filePath,
            String contentType,
            Long size
    ){ }

    public StoredFile storeIssueAttachment(MultipartFile file) {
        validateFile(file);

        try{
            Path issueUploadPath = Paths.get(uploadDir, "issues")
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(issueUploadPath);

            String originalFileName = cleanFileName(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFileName);
            String storedFileName = UUID.randomUUID() + fileExtension;

            Path targetLocation = issueUploadPath
                    .resolve(storedFileName)
                    .normalize();

            Files.copy(
                    file.getInputStream(),
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return new StoredFile(
                    originalFileName,
                    storedFileName,
                    targetLocation.toString(),
                    file.getContentType(),
                    file.getSize()
            );

        } catch(IOException exception) {
            throw new BadRequestException("Could not store file");
        }
    }

    public Path loadFile(String filePath) {
        Path path = Paths.get(filePath)
                .toAbsolutePath()
                .normalize();

        if (!Files.exists(path)) {
            throw new BadRequestException("File not found on server");
        }

        return path;
    }
}
