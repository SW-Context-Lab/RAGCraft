package lab.context.ragcraft.domain.custommodel;


import lab.context.ragcraft.api.custommodel.CustomModelDetailResponse;
import lab.context.ragcraft.api.custommodel.CustomModelListResponse;
import lab.context.ragcraft.domain.source.Source;
import lab.context.ragcraft.domain.source.SourceRepository;
import lab.context.ragcraft.domain.user.User;
import lab.context.ragcraft.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomModelService {

    private final CustomModelRepository customModelRepository;
    private final SourceRepository sourceRepository;
    private final UserRepository userRepository;

    @Transactional
    public CustomModel save(
            Long userId,
            Long sourceId,
            String modelType,
            String displayName,
            String description
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("USER_NOT_FOUND"));

        Source source = sourceRepository.findByIdAndUserId(sourceId, userId)
                .orElseThrow(() -> new IllegalStateException("SOURCE_NOT_FOUND"));

        CustomModel customModel = new CustomModel(
                user,
                source,
                modelType,
                displayName,
                description
        );

        return customModelRepository.save(customModel);
    }


    @Transactional(readOnly = true)
    public List<CustomModelListResponse> getCustomModels(Long userId) {

        // 사용자 존재 검증
        if (!userRepository.existsById(userId)) {
            throw new IllegalStateException("USER_NOT_FOUND");
        }

        return customModelRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(model -> new CustomModelListResponse(
                        model.getId(),
                        model.getDisplayName(),
                        model.getDescription()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomModelDetailResponse getCustomModel(
            Long userId,
            Long customModelId
    ) {
        // 사용자 존재 검증
        if (!userRepository.existsById(userId)) {
            throw new IllegalStateException("USER_NOT_FOUND");
        }

        CustomModel model = customModelRepository
                .findByIdAndUserId(customModelId, userId)
                .orElseThrow(() -> new IllegalStateException("CUSTOM_MODEL_NOT_FOUND"));

        return new CustomModelDetailResponse(
                model.getId(),
                model.getDisplayName(),
                model.getDescription(),
                model.getModelType(),
                model.getCreatedAt(),
                new CustomModelDetailResponse.SourceInfo(
                        model.getSource().getId(),
                        model.getSource().getDisplayName(),
                        model.getSource().getDescription()
                )
        );
    }

    @Transactional(readOnly = true)
    public String query(
            Long userId,
            Long customModelId,
            String question
    ) {
        // 사용자 존재 검증 (기존 컨셉 유지)
        if (!userRepository.existsById(userId)) {
            throw new IllegalStateException("USER_NOT_FOUND");
        }

        CustomModel model = customModelRepository
                .findByIdAndUserId(customModelId, userId)
                .orElseThrow(() -> new IllegalStateException("CUSTOM_MODEL_NOT_FOUND"));

        Long sourceId = model.getSource().getId();

        // 현재는 실제 질의 처리 안 함
        // 나중에 여기서 vector DB + LLM 호출로 교체 예정

        return "[TEMP ANSWER] sourceId=" + sourceId + ", question=" + question;
    }
}