package study.sunsuwedding.infra.storage;

import study.sunsuwedding.common.exception.BusinessException;
import study.sunsuwedding.common.exception.ErrorCode;

import static study.sunsuwedding.infra.storage.S3ErrorCode.*;

public class S3Exception extends BusinessException {

    public S3Exception(ErrorCode errorCode) {
        super(errorCode);
    }

    public static S3Exception emptyFile() {
        return new S3Exception(EMPTY_FILE);
    }

    public static S3Exception missingFileExtension() {
        return new S3Exception(MISSING_FILE_EXTENSION);
    }

    public static S3Exception invalidFileExtension() {
        return new S3Exception(INVALID_FILE_EXTENSION);
    }

    public static S3Exception s3UploadFailed() {
        return new S3Exception(S3_UPLOAD_FAILED);
    }

    public static S3Exception s3DeleteFailed() {
        return new S3Exception(S3_DELETE_FAILED);
    }
}
