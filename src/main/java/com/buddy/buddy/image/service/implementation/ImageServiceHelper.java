package com.buddy.buddy.image.service.implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.DTO.UploadImageDTO;
import com.buddy.buddy.image.entity.Image;
import com.buddy.buddy.image.entity.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class ImageServiceHelper {

    private static final Logger logger = LoggerFactory.getLogger(ImageServiceHelper.class.getName());
    @Value("${app.file.storage-path}")
    private String storagePath;

    public Image createImageEntity(UploadImageDTO dto, User user, String url, String blurredUrl, String mediaType, UUID uuid) {
        Image image = new Image();
        image.setUploadedDate(new Date());
        image.setDescription(dto.getDescription());
        image.setUser(user);
        image.setUrl(url);
        image.setBlurredUrl(blurredUrl);
        image.setOpen(dto.isOpen());
        image.setMediaType(detectMediaType(mediaType));
        image.setId(uuid);
        return image;
    }

    public String createBlurredImage(Path originalImagePath, String extension) {
        try {
            BufferedImage originalImage = ImageIO.read(originalImagePath.toFile());
            BufferedImage blurredImage = blurImage(originalImage);
            String blurredImageId = UUID.randomUUID().toString();
            String blurredFileName = blurredImageId + "." + extension;
            Path blurredImagePath = originalImagePath.getParent().resolve(blurredFileName);
            ImageIO.write(blurredImage, extension, blurredImagePath.toFile());
            logger.info("Blurred image saved to: {}", blurredImagePath);
            return blurredImageId;
        } catch (IOException e) {
            logger.error("Failed to create blurred image: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create blurred image");
        }
    }

    public Path saveFile(MultipartFile file, UUID uuid, String extension) throws IOException {
        Path uploadPath = Paths.get(storagePath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String savedFileName = uuid.toString() + "." + extension;
        Path filePath = uploadPath.resolve(savedFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filePath;
    }

    public String getFileExtension(MultipartFile file) {
        return Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf('.') + 1)
                .toLowerCase();
    }



    public BufferedImage blurImage(BufferedImage image) {
        // TODO: need optimization
        int edgeSize = 32;
        int extendedWidth = image.getWidth() + 2 * edgeSize;
        int extendedHeight = image.getHeight() + 2 * edgeSize;

        BufferedImage extendedImage = new BufferedImage(extendedWidth, extendedHeight, image.getType());
        Graphics2D g2d = extendedImage.createGraphics();

        g2d.drawImage(image, edgeSize, edgeSize, null);

        g2d.drawImage(image, edgeSize, 0, edgeSize + image.getWidth(), edgeSize, 0, 0, image.getWidth(), 1, null);
        g2d.drawImage(image, edgeSize, edgeSize + image.getHeight(), edgeSize + image.getWidth(), extendedHeight, 0, image.getHeight() - 1, image.getWidth(), image.getHeight(), null);

        g2d.drawImage(image, 0, edgeSize, edgeSize, edgeSize + image.getHeight(), 0, 0, 1, image.getHeight(), null);
        g2d.drawImage(image, edgeSize + image.getWidth(), edgeSize, extendedWidth, edgeSize + image.getHeight(), image.getWidth() - 1, 0, image.getWidth(), image.getHeight(), null);

        g2d.drawImage(image, 0, 0, edgeSize, edgeSize, 0, 0, 1, 1, null);
        g2d.drawImage(image, edgeSize + image.getWidth(), 0, extendedWidth, edgeSize, image.getWidth() - 1, 0, image.getWidth(), 1, null);
        g2d.drawImage(image, 0, edgeSize + image.getHeight(), edgeSize, extendedHeight, 0, image.getHeight() - 1, 1, image.getHeight(), null);
        g2d.drawImage(image, edgeSize + image.getWidth(), edgeSize + image.getHeight(), extendedWidth, extendedHeight, image.getWidth() - 1, image.getHeight() - 1, image.getWidth(), image.getHeight(), null);
        g2d.dispose();

        float[] matrix = new float[4096];
        Arrays.fill(matrix, 1.0f / 4096.0f);
        Kernel kernel = new Kernel(64, 64, matrix);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);

        BufferedImage blurredExtendedImage = new BufferedImage(extendedWidth, extendedHeight, image.getType());
        op.filter(extendedImage, blurredExtendedImage);

        return blurredExtendedImage.getSubimage(edgeSize, edgeSize, image.getWidth(), image.getHeight());
    }

    public void validateUploadImageDTO(UploadImageDTO uploadImageDTO) {
        if (uploadImageDTO.getTagSet().size() > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TagSet size too large");
        }
        if (uploadImageDTO.getDescription().length() > 2048) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description too long");
        }
    }

    private MediaType detectMediaType(String fileExtension) {
        if (fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png")) {
            return MediaType.IMAGE;
        }
        return MediaType.VIDEO;
    }

    public boolean isVideo(String fileExtension) {
        final Set<String> imageExtension = Set.of("mp4", "mov", "avi");
        return imageExtension.contains(fileExtension);
    }

    public static void validateFile(MultipartFile file) {
        final Set<String> allowedExtensions = Set.of("jpg", "jpeg", "png", "mp4", "mov");
        final long maxFileSize = 100 * 1024 * 1024; // 100 MB

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file provided");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File name is missing");
        }

        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        if (!allowedExtensions.contains(fileExtension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file extension. Allowed extensions: " + allowedExtensions);
        }

        if (file.getSize() > maxFileSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size exceeds the limit of " + (maxFileSize / (1024 * 1024)) + " MB");
        }
    }

}
