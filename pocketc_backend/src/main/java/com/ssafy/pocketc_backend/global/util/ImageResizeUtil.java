package com.ssafy.pocketc_backend.global.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.exif.ExifIFD0Directory;
import org.imgscalr.Scalr;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

public class ImageResizeUtil {

    private static final int TARGET_SIZE = 128;

    public static byte[] resizeImage(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();

        // 원본 로드
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
        if (image == null) throw new IOException("Unsupported image format");

        // 1) EXIF Orientation 적용(회전 보정)
        int orientation = readExifOrientation(bytes);
        image = applyOrientation(image, orientation);

        // 2) 정사각형 중앙 크롭
        int min = Math.min(image.getWidth(), image.getHeight());
        int x = (image.getWidth() - min) / 2;
        int y = (image.getHeight() - min) / 2;
        BufferedImage cropped = Scalr.crop(image, x, y, min, min);

        //더 작은 이미지가 들어왔을 경우 업스케일 방지
        if (cropped.getWidth() <= TARGET_SIZE && cropped.getHeight() <= TARGET_SIZE) {
            return convertToJpeg(cropped, 0.8f);
        }
        // 3) 리사이즈
        BufferedImage resized = Scalr.resize(
                cropped,
                Scalr.Method.QUALITY,
                TARGET_SIZE,
                TARGET_SIZE,
                Scalr.OP_ANTIALIAS
        );


        // 4) JPG 강제변환, 압축 품질 0.8f
        return convertToJpeg(resized, 0.8f);
    }

    private static int readExifOrientation(byte[] bytes) {
        try {
            var metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(bytes));
            var dir = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (dir != null && dir.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                return dir.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            }
        } catch (Exception ignored) {}
        return 1; // 기본값
    }

    private static BufferedImage applyOrientation(BufferedImage img, int orientation) {
        return switch (orientation) {
            case 3 -> Scalr.rotate(img, Scalr.Rotation.CW_180);
            case 6 -> Scalr.rotate(img, Scalr.Rotation.CW_90);
            case 8 -> Scalr.rotate(img, Scalr.Rotation.CW_270);
            default -> img;
        };
    }

    private static byte[] convertToJpeg(BufferedImage image, float quality) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) throw new IllegalStateException("No JPEG writers found");

        ImageWriter writer = writers.next();
        try (MemoryCacheImageOutputStream ios = new MemoryCacheImageOutputStream(out)) {
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality);
            }

            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
        return out.toByteArray();
    }
}
