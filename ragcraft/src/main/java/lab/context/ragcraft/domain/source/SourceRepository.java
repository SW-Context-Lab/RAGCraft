package lab.context.ragcraft.domain.source;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SourceRepository extends JpaRepository<Source, Long> {
    List<Source> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 소유자 검증
    Optional<Source> findByIdAndUserId(Long id, Long userId);
}
