package lab.context.ragcraft.api.source;

import jakarta.servlet.http.HttpSession;
import lab.context.ragcraft.domain.source.Source;
import lab.context.ragcraft.domain.source.SourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
     * - file: 업로드할 파일 (필수)
     * - displayName: 사용자 정의 소스 이름 (필수)
     * - description: 소스 설명 (선택)
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("displayName") String displayName,
            @RequestParam(value = "description", required = false) String description,
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

        // 3. displayName 유효성 체크
        if (displayName == null || displayName.isBlank()) {
            return ResponseEntity.badRequest().body("DISPLAY_NAME_REQUIRED");
        }

        // 4. 업로드 처리
        Source source = sourceService.upload(
                userId,
                displayName,
                description,
                file
        );

        // 5. 최소 응답
        return ResponseEntity
                .status(201)
                .body(new SourceUploadResponse(source.getId()));
    }



    /**
     * 소스 목록 조회 API (메타데이터)
     *
     * @param session
     * @return
     */
    @GetMapping
    public ResponseEntity<List<SourceListResponse>> getSources(HttpSession session) {

        Long userId = (Long) session.getAttribute(LOGIN_USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        List<SourceListResponse> sources = sourceService.getSources(userId);
        return ResponseEntity.ok(sources);
    }

    /**
     *
     * @param sourceId
     * @param session
     * @return
     */
    @GetMapping("/{sourceId}")
    public ResponseEntity<SourceDetailResponse> getSource(
            @PathVariable Long sourceId,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute(LOGIN_USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        SourceDetailResponse response = sourceService.getSource(userId, sourceId);
        return ResponseEntity.ok(response);
    }
}