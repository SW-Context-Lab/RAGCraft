package lab.context.ragcraft.api.custommodel;

import java.time.LocalDateTime;

public record CustomModelDetailResponse(
        Long id,
        String displayName,
        String description,
        String modelType,
        LocalDateTime createdAt,
        SourceInfo source
) {
    public record SourceInfo(
            Long id,
            String displayName,
            String description
    ) {}
}