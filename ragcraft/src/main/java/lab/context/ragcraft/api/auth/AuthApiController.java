package lab.context.ragcraft.api.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lab.context.ragcraft.domain.user.User;
import lab.context.ragcraft.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private static final String LOGIN_USER_ID = "LOGIN_USER_ID";

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인 API
     *
     * POST /api/auth/login
     *
     * 요청:
     * {
     *   "email": "...",
     *   "password": "..."
     * }
     *
     * 응답:
     * - 200 OK  → 로그인 성공 (세션 생성됨)
     * - 401     → 인증 실패
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        User user = userService.findByEmailOptional(request.email());
        if (user == null) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        // 세션 재생성
        HttpSession oldSession = httpRequest.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(LOGIN_USER_ID, user.getId());

        return ResponseEntity.ok().build();
    }

    /**
     * 로그아웃 API
     *
     * POST /api/auth/logout
     *
     * - 세션 제거
     * - 클라이언트 상태는 신경 쓰지 않음
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        // 기존 세션 조회 (없으면 null)
        HttpSession session = request.getSession(false);

        // 세션이 존재하면 완전히 제거
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok().build();
    }

    /**
     * 현재 로그인 사용자 확인 API
     *
     * GET /api/auth/me
     *
     * 프론트엔드 용도:
     * - 앱 시작 시 로그인 상태 확인
     * - 401 → 로그인 화면 이동
     * - 200 → 로그인 유지
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {

        // 세션에서 로그인 사용자 ID 조회
        Object userId = session.getAttribute(LOGIN_USER_ID);

        // 로그인 상태 아님
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        // 로그인 상태
        return ResponseEntity.ok(userId);
    }

    /**
     * 회원가입 API
     *
     * POST /api/auth/signup
     *
     * 성공:
     * - 201 Created
     *
     * 실패:
     * - 400 Bad Request (입력값 누락)
     * - 409 Conflict (이메일 중복)
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {

        // 1. 최소 입력값 검증
        if (request.email() == null || request.password() == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            userService.signup(request.email(), request.password());
            return ResponseEntity.status(201).build();

        } catch (IllegalStateException e) {

            // 이메일 중복
            if ("EMAIL_ALREADY_EXISTS".equals(e.getMessage())) {
                return ResponseEntity
                        .status(409)
                        .body("EMAIL_ALREADY_EXISTS");
            }

            throw e;
        }
    }
}