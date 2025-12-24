package lab.context.ragcraft.api.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * API 인증 인터셉터
 *
 * 역할:
 * - 세션 기반 로그인 여부 확인
 * - 로그인 안 된 경우 API 접근 차단
 */
public class LoginCheckInterceptor implements HandlerInterceptor {

    private static final String LOGIN_USER_ID = "LOGIN_USER_ID";

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        // 기존 세션 조회 (없으면 null)
        HttpSession session = request.getSession(false);

        // 로그인 정보 없음 → 인증 실패
        if (session == null || session.getAttribute(LOGIN_USER_ID) == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 로그인 상태 → 요청 계속 진행
        return true;
    }
}
