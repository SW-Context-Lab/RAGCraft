package lab.context.ragcraft.domain.custommodel.chat;

import lab.context.ragcraft.domain.custommodel.CustomModel;
import lab.context.ragcraft.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "custom_model_chat",
        indexes = {
                @Index(name = "idx_chat_user_model", columnList = "user_id, custom_model_id"),
                @Index(name = "idx_chat_created_at", columnList = "created_at")
        }
)
public class CustomModelChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 질문한 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 사용한 커스텀 모델
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_model_id", nullable = false)
    private CustomModel customModel;

    // USER, ASSISTANT
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatRole role;

    // 질문 or 답변 내용
    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected CustomModelChat() {
    }

    public CustomModelChat(
            User user,
            CustomModel customModel,
            ChatRole role,
            String content
    ) {
        this.user = user;
        this.customModel = customModel;
        this.role = role;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }
}
