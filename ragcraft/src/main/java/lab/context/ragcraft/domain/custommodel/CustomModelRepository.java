package lab.context.ragcraft.domain.custommodel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomModelRepository extends JpaRepository<CustomModel, Long> {
    List<CustomModel> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<CustomModel> findByIdAndUserId(Long id, Long userId);
    void deleteBySourceId(Long sourceId);
}