package lab.context.ragcraft.api.custommodel;

import jakarta.servlet.http.HttpSession;
import lab.context.ragcraft.domain.custommodel.CustomModel;
import lab.context.ragcraft.domain.custommodel.CustomModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/custom-models")
public class CustomModelController {

    private static final String LOGIN_USER_ID = "LOGIN_USER_ID";

    private final CustomModelService customModelService;

    /**
     * 커스텀 모델 생성 API
     *
     * POST /api/custom-models
     * Content-Type: application/json
     *
     * Request Body:
     * {
     *   "sourceId": Long,          // 사용할 소스 ID (필수)
     *   "modelType": String,       // 선택할 모델 타입 (필수)
     *   "displayName": String,     // 커스텀 모델 이름 (필수)
     *   "description": String     // 커스텀 모델 설명 (선택)
     * }
     *
     * Description:
     * - 로그인한 사용자가 자신의 소스 1개를 선택하여 커스텀 모델을 생성함
     * - sourceId는 반드시 로그인한 사용자의 소스여야 함
     * - 하나의 소스로 여러 커스텀 모델 생성 가능함
     *
     * Response:
     * - 201 Created: 커스텀 모델 생성 성공
     * - 400 BAD_REQUEST: displayName 없음
     * - 401 Unauthorized: 로그인하지 않은 경우
     * - 404 Not Found:
     *   - USER_NOT_FOUND: 사용자 없음
     *   - SOURCE_NOT_FOUND: 소스가 없거나 사용자 소유가 아님
     */
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody CustomModelCreateRequest request,
            HttpSession session
    ) {
        // 1. 로그인 여부 확인
        Long userId = (Long) session.getAttribute(LOGIN_USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        // 2. displayName 유효성 체크
        if (request.displayName() == null || request.displayName().isBlank()) {
            return ResponseEntity.badRequest().body("DISPLAY_NAME_REQUIRED");
        }

        CustomModel customModel = customModelService.save(
                userId,
                request.sourceId(),
                request.modelType(),
                request.displayName(),
                request.description()
        );

        return ResponseEntity
                .status(201)
                .body(customModel.getId());
    }

    /**
     * 커스텀 모델 목록 조회 API
     *
     * GET /api/custom-models
     *
     * Description:
     * - 로그인한 사용자가 생성한 커스텀 모델 목록을 조회함
     * - 첫 페이지에서 사용하기 위한 간단 리스트 조회용 API
     * - displayName, description만 반환함
     *
     * Response:
     * [
     *   {
     *     "id": Long,
     *     "displayName": String,
     *     "description": String
     *   }
     * ]
     *
     * - 200 OK: 조회 성공
     * - 401 Unauthorized: 로그인하지 않은 경우
     */
    @GetMapping
    public ResponseEntity<?> getCustomModels(HttpSession session) {

        Long userId = (Long) session.getAttribute(LOGIN_USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        List<CustomModelListResponse> customModelListResponses = customModelService.getCustomModels(userId);

        return ResponseEntity.ok(customModelListResponses);
    }

    /**
     * 커스텀 모델 상세 조회 API
     *
     * GET /api/custom-models/{customModelId}
     *
     * Description:
     * - 커스텀 모델 1개의 상세 정보를 조회함
     * - 해당 모델이 사용하는 소스 정보도 함께 반환함
     *
     * Response:
     * - 200 OK: 조회 성공
     * - 401 Unauthorized: 로그인하지 않은 경우
     * - 404 Not Found:
     *   - USER_NOT_FOUND
     *   - CUSTOM_MODEL_NOT_FOUND
     */
    @GetMapping("/{customModelId}")
    public ResponseEntity<?> getCustomModel(
            @PathVariable Long customModelId,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute(LOGIN_USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(
                customModelService.getCustomModel(userId, customModelId)
        );
    }


    @PostMapping("/{customModelId}/query")
    public ResponseEntity<?> query(
            @PathVariable Long customModelId,
            @RequestBody String question,
            HttpSession session
    ) throws IOException {
        Long userId = (Long) session.getAttribute(LOGIN_USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest().body("QUESTION_REQUIRED");
        }

        String answer = customModelService.query(
                userId,
                customModelId,
                question
        );

        return ResponseEntity.ok(answer);
    }

    @DeleteMapping("/{customModelId}")
    public ResponseEntity<Void> deleteCustomModel(
            @PathVariable Long customModelId,
            HttpSession session
    ){
        Long userId = (Long) session.getAttribute(LOGIN_USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        customModelService.deleteCustomModel(userId, customModelId);
        return ResponseEntity.noContent().build();
    }
}