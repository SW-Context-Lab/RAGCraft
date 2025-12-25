package lab.context.ragcraft.api.source;

public record SourceDetailResponse(
        Long id,
        String displayName,
        String description,
        String originalFilename,
        long size,
        String contentType,
        String downloadUrl
) {}