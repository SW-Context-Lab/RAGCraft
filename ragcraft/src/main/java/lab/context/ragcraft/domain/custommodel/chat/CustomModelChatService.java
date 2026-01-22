package lab.context.ragcraft.domain.custommodel.chat;

import lab.context.ragcraft.api.custommodel.CustomModelChatResponse;
import lab.context.ragcraft.domain.custommodel.CustomModel;
import lab.context.ragcraft.domain.custommodel.CustomModelRepository;
import lab.context.ragcraft.domain.user.User;
import lab.context.ragcraft.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomModelChatService {

    private final CustomModelChatRepository chatRepository;
    private final UserRepository userRepository;
    private final CustomModelRepository customModelRepository;

    @Transactional(readOnly = true)
    public List<CustomModelChatResponse> getChats(
            Long userId,
            Long customModelId
    ) {
        // 사용자 검증
        if (!userRepository.existsById(userId)) {
            throw new IllegalStateException("USER_NOT_FOUND");
        }

        // 모델 소유 검증
        if (!customModelRepository.existsByIdAndUserId(customModelId, userId)) {
            throw new IllegalStateException("CUSTOM_MODEL_NOT_FOUND");
        }

        return chatRepository
                .findByUserIdAndCustomModelIdOrderByCreatedAtAsc(userId, customModelId)
                .stream()
                .map(chat -> new CustomModelChatResponse(
                        chat.getRole(),
                        chat.getContent(),
                        chat.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public void saveChatHistory(
            User user,
            CustomModel model,
            String question,
            String answer
    ) {
        // USER 메시지
        chatRepository.save(
                new CustomModelChat(
                        user,
                        model,
                        ChatRole.USER,
                        question
                )
        );

        // ASSISTANT 메시지
        chatRepository.save(
                new CustomModelChat(
                        user,
                        model,
                        ChatRole.ASSISTANT,
                        answer
                )
        );
    }
}