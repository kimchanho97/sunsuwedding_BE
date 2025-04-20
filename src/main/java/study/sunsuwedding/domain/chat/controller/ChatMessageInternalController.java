package study.sunsuwedding.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import study.sunsuwedding.infra.storage.S3ImageService;
import study.sunsuwedding.infra.storage.S3UploadResultDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/chat")
public class ChatMessageInternalController {

    private final S3ImageService s3ImageService;
    
    @PostMapping("/image-upload")
    public ResponseEntity<S3UploadResultDto> uploadChatImage(@RequestPart("file") MultipartFile file) {
        S3UploadResultDto result = s3ImageService.uploadImage(file);
        return ResponseEntity.ok(result);
    }
}
