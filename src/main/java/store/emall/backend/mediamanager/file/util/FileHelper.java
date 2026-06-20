package store.emall.backend.mediamanager.file.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import store.emall.backend.mediamanager.file.dto.FileDto;
import store.emall.backend.mediamanager.file.FileSize;
import store.emall.backend.mediamanager.file.visibility.MediaVisibility;
import store.emall.backend.mediamanager.storage.CloudStorage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class FileHelper {


    // TODO : optimize the resizing process
    public static MultipartFile resizeImage(MultipartFile originalFile, int targetWidth, int targetHeight, String extension) throws IOException {

        // 1. Read the incoming MultipartFile into a BufferedImage
        BufferedImage originalImage = ImageIO.read(originalFile.getInputStream());
        if (originalImage == null) {
            throw new IllegalArgumentException("The provided file is not a valid image format.");
        }

        // 2. Determine image type (preserve transparency for PNGs)
        int imageType = (extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("gif"))
                ? BufferedImage.TYPE_INT_ARGB
                : BufferedImage.TYPE_INT_RGB;

        // 3. Create a new, blank image with the target dimensions
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, imageType);
        Graphics2D g2d = resizedImage.createGraphics();

        // Apply quality rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 4. Draw the original image scaled down onto the new image
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        // 5. Write the resized image to a ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Default to "jpg" if extension is missing to prevent ImageIO errors
        String formatName = (extension == null || extension.isEmpty()) ? "jpg" : extension;
        ImageIO.write(resizedImage, formatName, baos);

        // 6. Wrap the resulting byte array in our custom MultipartFile
        return new ByteArrayMultipartFile(
                originalFile.getName(),
                originalFile.getOriginalFilename(),
                originalFile.getContentType(),
                baos.toByteArray()
        );
    }

    // TODO : optimize the resizing process
    public static MultipartFile resizeImageProportionally(MultipartFile originalFile, int maxWidth, int maxHeight, String extension) throws IOException {

        // 1. Read the original image
        BufferedImage originalImage = ImageIO.read(originalFile.getInputStream());
        if (originalImage == null) {
            throw new IllegalArgumentException("The provided file is not a valid image format.");
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 2. Calculate the new dimensions preserving the aspect ratio
        int newWidth = originalWidth;
        int newHeight = originalHeight;

        // Only scale down if the image is larger than the bounding box
        if (originalWidth > maxWidth || originalHeight > maxHeight) {
            double widthRatio = (double) maxWidth / originalWidth;
            double heightRatio = (double) maxHeight / originalHeight;

            // Pick the smaller ratio to ensure it fits completely inside the bounding box
            double scaleRatio = Math.min(widthRatio, heightRatio);

            newWidth = (int) Math.round(originalWidth * scaleRatio);
            newHeight = (int) Math.round(originalHeight * scaleRatio);
        }

        // 3. Determine image type (preserve transparency for PNGs)
        int imageType = (extension != null && (extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("gif")))
                ? BufferedImage.TYPE_INT_ARGB
                : BufferedImage.TYPE_INT_RGB;

        // 4. Draw the resized image
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, imageType);
        Graphics2D g2d = resizedImage.createGraphics();

        // High-quality rendering hints
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        // 5. Convert back to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String formatName = (extension == null || extension.isEmpty()) ? "jpg" : extension;
        ImageIO.write(resizedImage, formatName, baos);

        // 6. Return using the ByteArrayMultipartFile class we made earlier
        return new ByteArrayMultipartFile(
                originalFile.getName(),
                originalFile.getOriginalFilename(),
                originalFile.getContentType(),
                baos.toByteArray()
        );
    }


    public static String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    public static boolean isImage(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            return image != null;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    public static String generatePresignedUrl(UUID id, FileSize size, CloudStorage cloudStorage) {

        String key = generateFileKey(
                id,
                size,
                MediaVisibility.PRIVATE
        );
        return cloudStorage.generatePresignedUrl(key);
    }

    public static FileDto injectPresignedUrlToTheDto(FileDto fileDto, boolean originalOnly, CloudStorage cloudStorage) {
        fileDto.setOriginalFileUrl(generatePresignedUrl(fileDto.getId(), FileSize.OPTIMIZED_ORIGINAL, cloudStorage));
        if(!isImage(fileDto.getMimeType()))
            return fileDto;
        if(originalOnly)
            return fileDto;

        fileDto.setMediumFileUrl(generatePresignedUrl(fileDto.getId(), FileSize.MEDIUM, cloudStorage));
        fileDto.setSmallFileUrl(generatePresignedUrl(fileDto.getId(), FileSize.SMALL, cloudStorage));
        return fileDto;
    }


    public static String generateFileKey(UUID uuid, FileSize size, MediaVisibility visibility) {
        String visibilityPrefix = MediaVisibility.PUBLIC.equals(visibility) ? "public" : "private";
        return visibilityPrefix + "/uploads/" + uuid + "/" + size.getSize();
    }

    public static String generateFileKey(UUID uuid, FileSize size) {
        return generateFileKey(uuid, size, MediaVisibility.PRIVATE);
    }

    public static String generateLegacyFileKey(UUID uuid, FileSize size) {
        return size.getSize() + "/" + uuid;
    }
}
