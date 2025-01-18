package com.buddy.buddy.image.service.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class VideoFrameProcessor {

    @Value("${app.file.storage-path}")
    private String storagePath;
    private final ImageServiceHelper imageServiceHelper = new ImageServiceHelper();
    private static final Logger logger = LoggerFactory.getLogger(VideoFrameProcessor.class.getName());
    // TODO: This is working very slow need better solution
    public void extractFrame(String videoPath, String outputImagePath, String uuidWithExtension) throws IOException {
        logger.info("Start extracting frame from " + videoPath);
        String framePath = "first_frame.png";
        String ffmpegCommand = String.format("ffmpeg -i %s -frames:v 1 -q:v 2 %s", videoPath, framePath);

        try {
            Process process = Runtime.getRuntime().exec(ffmpegCommand);
            process.waitFor();
        } catch (Exception e) {
            throw new IOException("Failed to extract frame using FFmpeg: " + e.getMessage());
        }

        BufferedImage image = ImageIO.read(new File(framePath));

        BufferedImage blurredImage = imageServiceHelper.blurImage(image);

        ImageIO.write(blurredImage, "png", new File(outputImagePath));
        saveBufferedImage(blurredImage, uuidWithExtension);


        Files.deleteIfExists(Paths.get(framePath));
        logger.info("Processed frame and blurred");
    }
    private void saveBufferedImage(BufferedImage image, String uuidWithExtension) throws IOException {
        Path uploadPath = Paths.get(storagePath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(uuidWithExtension);

        ImageIO.write(image, "png", filePath.toFile());
        logger.info("Saved blurred image to: " + filePath);
    }
}
