package com.decoupledx.common.web;

import com.decoupledx.common.error.BusinessException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

/**
 * Stores uploaded images into the configured directory ({@code
 * app.uploads.dir}) and returns the web path under /uploads/*. Validates the
 * type/content and the app-side size cap BEFORE the servlet container's raw
 * error (the multipart limit is deliberately sized slightly higher so the
 * friendly BusinessException wins). Generated UUID file names make served
 * URLs unguessable. Serving the directory is routing policy — application
 * owned.
 */
@Slf4j
public class Uploads {

    /** 5 MB — keep multipart limits slightly above (application.yml). */
    static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024;

    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final String uploadDir;

    public Uploads(@Value("${app.uploads.dir:uploads}") String uploadDir) {
        this.uploadDir = uploadDir;
        log.info("Uploads directory (resolved): {}",
                Path.of(uploadDir).toAbsolutePath().normalize());
    }

    /**
     * Validates + stores the upload, returning its web path
     * (e.g. /uploads/logo-<uuid>.png). prefix keeps generations tidy
     * (photo-…, logo-…).
     */
    public String save(MultipartFile file, String prefix) {
        String extension = switch (file.getContentType() == null ? "" : file.getContentType()) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/webp" -> "webp";
            default -> throw new BusinessException(UploadErrorCode.INVALID_UPLOAD_IMAGE_TYPE);
        };
        if (file.isEmpty()) {
            throw new BusinessException(UploadErrorCode.INVALID_UPLOAD_IMAGE_TYPE);
        }
        if (file.getSize() > MAX_IMAGE_BYTES) {
            throw new BusinessException(UploadErrorCode.UPLOAD_IMAGE_TOO_LARGE);
        }
        try {
            Path dir = Path.of(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String fileName = prefix + UUID.randomUUID() + "." + extension;
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, dir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            }
            return "/uploads/" + fileName;
        } catch (IOException failure) {
            log.error("Upload store failed dir={} prefix={}", uploadDir, prefix, failure);
            throw new BusinessException(UploadErrorCode.UPLOAD_STORE_FAILED);
        }
    }
}
