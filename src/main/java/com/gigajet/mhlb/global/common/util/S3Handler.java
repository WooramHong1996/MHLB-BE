package com.gigajet.mhlb.global.common.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.gigajet.mhlb.global.exception.CustomException;
import com.gigajet.mhlb.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Handler {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    private final ImageUtil imageUtil;

    // upload 컴포넌트
    public String upload(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null) {
            throw new CustomException(ErrorCode.NULL_MULTIPART_FILE);
        }

        String originalFilename = multipartFile.getOriginalFilename();

        File uploadFile = convert(multipartFile).orElseThrow(() -> new CustomException(ErrorCode.FAIL_CONVERT));

        if (!imageUtil.checkMimeType(uploadFile)) {
            removeNewFile(uploadFile);
            throw new CustomException(ErrorCode.NOT_IMAGE);
        }

        imageUtil.resizing(uploadFile);

        String uploadImageUrl = putS3(uploadFile, UUID.randomUUID() + "-" + originalFilename);
        removeNewFile(uploadFile);

        return uploadImageUrl;
    }

    // 기존 파일 삭제
    public void delete(String fileName) {
        amazonS3.deleteObject(bucket, fileName.substring(fileName.lastIndexOf('/') + 1));
        log.info(fileName.substring(fileName.lastIndexOf('/') + 1));
    }

    // S3 upload
    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile)
                        .withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());

        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }

            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일 삭제");
        } else {
            log.info("파일 삭제 실패");
        }
    }

}
