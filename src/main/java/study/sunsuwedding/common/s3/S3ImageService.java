package study.sunsuwedding.common.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3ImageService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final S3Client s3Client;

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp");

    /**
     * 이미지 업로드 후 S3 URL 반환
     *
     * @param file 업로드할 이미지 파일
     * @return 업로드된 이미지의 S3 URL
     */
    public String uploadImage(MultipartFile file) {
        if (file.isEmpty() || Objects.isNull(file.getOriginalFilename())) {
            throw S3Exception.emptyFile();
        }

        String storedFileName = generateUniqueFileName(file.getOriginalFilename());

        try {
            uploadFileToS3(file, storedFileName);
            return generateS3Url(storedFileName);
        } catch (IOException e) {
            throw S3Exception.ioExceptionOnImageUpload();
        }
    }

    /**
     * S3에서 이미지 삭제
     *
     * @param fileName 삭제할 이미지 파일명 (S3 Key)
     */
    public void deleteImage(String fileName) throws Exception {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteRequest);
        } catch (Exception e) {
            throw S3Exception.deleteObjectException();
        }
    }

    private String generateUniqueFileName(String originalFilename) {
        String extension = validateAndExtractExtension(originalFilename);
        String uniqueName = UUID.randomUUID().toString().replace("-", "").substring(0, 15);
        return "uploads/" + uniqueName + "_" + System.currentTimeMillis() + "." + extension;
    }

    private String validateAndExtractExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw S3Exception.noFileExtension();
        }

        String extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw S3Exception.invalidFileExtension();
        }
        return extension;
    }

    private String generateS3Url(String storedFileName) {
        GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(storedFileName)
                .build();

        return s3Client.utilities().getUrl(getUrlRequest).toString();
    }

    /**
     * S3에 파일 업로드
     *
     * @param file           업로드할 파일
     * @param storedFileName 저장할 S3 파일명
     */
    private void uploadFileToS3(MultipartFile file, String storedFileName) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .contentType(file.getContentType())
                .key(storedFileName)
                .build();

        RequestBody requestBody = RequestBody.fromBytes(file.getBytes());
        s3Client.putObject(putObjectRequest, requestBody);
    }
    
}
