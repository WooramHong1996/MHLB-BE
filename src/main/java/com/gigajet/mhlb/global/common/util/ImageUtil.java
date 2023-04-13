package com.gigajet.mhlb.global.common.util;

import com.gigajet.mhlb.global.exception.CustomException;
import com.gigajet.mhlb.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import net.coobird.thumbnailator.resizers.configurations.ScalingMode;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageUtil {

    private final Tika tika = new Tika();

    // 리사이징
    public void resizing(File file) {
        try {
            Thumbnails.of(file)
                    .size(256, 256)
                    .scalingMode(ScalingMode.BICUBIC)
                    .outputQuality(0.8)
                    .crop(Positions.CENTER)
                    .allowOverwrite(true)
                    .toFile(file.getName());
        } catch (IOException e) {
            log.error("resizing error : {}", e.getMessage());
            throw new CustomException(ErrorCode.RESIZING_FAILED);
        }
    }

    public boolean checkMimeType(File file) throws IOException {
        try {
            List<String> mimeTypeList = Arrays.asList("image/jpeg", "image/png", "image/jpg", "image/gif");

            String fileMimeType = tika.detect(file);
            log.info("mime type : {}", fileMimeType);

            return mimeTypeList.stream().anyMatch(mimeType -> mimeType.equalsIgnoreCase(fileMimeType));

        } catch (IOException exception) {
            log.error(exception.getMessage());
            return false;
        }
    }
}
