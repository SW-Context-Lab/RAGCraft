package lab.context.ragcraft.api.auth;

/**
 * 회원가입 API 요청 DTO
 */
public record SignupRequest(

        // 사용자 이메일 (로그인 ID)
        String email,

        // 평문 비밀번호
        String password

) {}
