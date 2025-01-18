package com.buddy.buddy.image.service.implementation;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class ImageServiceHelper {

    private static final Logger logger = LoggerFactory.getLogger(ImageServiceHelper.class.getName());


    // TODO: Move all helper method from ImageServiceImplementation here

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

}
