package lab.context.ragcraft.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인용 사용자 조회
     */
    public User findByEmailOptional(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }


    /**
     * 회원가입
     * - 이메일 중복 체크
     * - 비밀번호 암호화
     */
    @Transactional
    public User signup(String email, String rawPassword) {

        // 이메일 중복 체크
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("EMAIL_ALREADY_EXISTS");
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(email, encodedPassword);
        return userRepository.save(user);
    }
}
