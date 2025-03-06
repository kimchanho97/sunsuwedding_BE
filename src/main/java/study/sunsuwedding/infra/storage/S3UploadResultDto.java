package study.sunsuwedding.infra.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class S3UploadResultDto {

    private String fileName;
    private String fileUrl;
}
