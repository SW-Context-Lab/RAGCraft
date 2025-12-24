package lab.context.ragcraft.domain.source;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SourceRepository extends JpaRepository<Source, Long> {
    List<Source> findAllByUserId(Long userId);
}
