package study.sunsuwedding;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    @RequestMapping("/api/test")
    public String testEndpoint() {
        return "테스트 성공";
    }
}
