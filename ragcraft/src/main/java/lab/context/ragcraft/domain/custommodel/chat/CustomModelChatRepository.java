package lab.context.ragcraft.domain.custommodel.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomModelChatRepository
        extends JpaRepository<CustomModelChat, Long> {

    List<CustomModelChat> findByUserIdAndCustomModelIdOrderByCreatedAtAsc(
            Long userId,
            Long customModelId
    );
}
