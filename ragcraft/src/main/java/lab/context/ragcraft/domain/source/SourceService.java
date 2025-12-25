package lab.context.ragcraft.domain.source;

import lab.context.ragcraft.api.source.SourceDetailResponse;
import lab.context.ragcraft.api.source.SourceListResponse;
import lab.context.ragcraft.domain.user.User;
import lab.context.ragcraft.domain.user.UserRepository;
import lab.context.ragcraft.storage.FileStorage;
import lab.context.ragcraft.storage.UploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SourceService {

    private final SourceRepository sourceRepository;
    private final UserRepository userRepository;
    private final FileStorage fileStorage;

    @Transactional
    public Source upload(
            Long userId,
            String displayName,
            String description,
            MultipartFile file
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("USER_NOT_FOUND"));

        UploadResult result = fileStorage.upload(file, "sources/" + userId);

        Source source = new Source(
                user,
                displayName,
                description,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                result.key(),
                result.url()
        );

        return sourceRepository.save(source);
    }

    @Transactional(readOnly = true)
    public List<SourceListResponse> getSources(Long userId) {
        return sourceRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(source -> new SourceListResponse(
                        source.getId(),
                        source.getDisplayName(),
                        source.getDescription()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public SourceDetailResponse getSource(Long userId, Long sourceId) {

        Source source = sourceRepository.findByIdAndUserId(sourceId, userId)
                .orElseThrow(() -> new IllegalStateException("SOURCE_NOT_FOUND"));

        // presigned URL 생성 (만료 5분)
        String downloadUrl = fileStorage.generatePresignedUrl(
                source.getS3Key(),
                300
        );

        return new SourceDetailResponse(
                source.getId(),
                source.getDisplayName(),
                source.getDescription(),
                source.getOriginalFilename(),
                source.getSize(),
                source.getContentType(),
                downloadUrl
        );
    }
}
