package lab.context.ragcraft.api.custommodel;

public record CustomModelCreateRequest(
        Long sourceId,
        String modelType,
        String displayName,
        String description
) {}