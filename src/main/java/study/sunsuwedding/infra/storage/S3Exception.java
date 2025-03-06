package study.sunsuwedding.infra.storage;

import study.sunsuwedding.common.exception.BusinessException;
import study.sunsuwedding.common.exception.ErrorCode;

public class S3Exception extends BusinessException {

    public S3Exception(ErrorCode errorCode) {
        super(errorCode);
    }

    public static S3Exception emptyFile() {
        return new S3Exception(S3ErrorCode.EMPTY_FILE);
    }

    public static S3Exception ioExceptionOnImageUpload() {
        return new S3Exception(S3ErrorCode.IO_EXCEPTION_ON_IMAGE_UPLOAD);
    }

    public static S3Exception noFileExtension() {
        return new S3Exception(S3ErrorCode.NO_FILE_EXTENSION);
    }

    public static S3Exception invalidFileExtension() {
        return new S3Exception(S3ErrorCode.INVALID_FILE_EXTENSION);
    }

    public static S3Exception putObjectException() {
        return new S3Exception(S3ErrorCode.PUT_OBJECT_EXCEPTION);
    }

    public static Exception deleteObjectException() {
        return new S3Exception(S3ErrorCode.DELETE_OBJECT_EXCEPTION);
    }
}
