package lab.context.ragcraft.api.custommodel;

import lab.context.ragcraft.domain.custommodel.chat.ChatRole;

import java.time.LocalDateTime;

public record CustomModelChatResponse(
        ChatRole role,
        String content,
        LocalDateTime createdAt
) {
}