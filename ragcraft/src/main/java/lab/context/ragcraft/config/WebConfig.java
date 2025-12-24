package lab.context.ragcraft.config;


import lab.context.ragcraft.api.auth.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new LoginCheckInterceptor())
                // 인증이 필요한 API만 대상
                .addPathPatterns("/api/**")

                // 인증 예외 API
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/logout",
                        "/api/auth/me",
                        "/api/auth/signup"
                );
    }
}
