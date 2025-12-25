package lab.context.ragcraft.api.source;

import jakarta.servlet.http.HttpSession;
import lab.context.ragcraft.domain.source.Source;
import lab.context.ragcraft.domain.source.SourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sources")
public class SourceApiController {

    private static final String LOGIN_USER_ID = "LOGIN_USER_ID";

    private final SourceService sourceService;

    /**
     * 소스 업로드 API
     *
     * POST /api/sources
     * Content-Type: multipart/form-data
     *
     * form-data:
     * - file: 업로드할 파일
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            HttpSession session
    ) {

        // 1. 로그인 여부 확인
        Long userId = (Long) session.getAttribute(LOGIN_USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        // 2. 파일 유효성 체크
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("FILE_REQUIRED");
        }

        // 3. 업로드 처리
        Source source = sourceService.upload(userId, file);

        // 4. 최소 응답 (ID만 반환)
        return ResponseEntity
                .status(201)
                .body(new SourceUploadResponse(source.getId()));
    }
}