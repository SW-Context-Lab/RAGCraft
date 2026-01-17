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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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

        Source saved = sourceRepository.save(source);

        // 데이터 처리 서버로 전송
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> payload = Map.of(
                "user_id", userId.toString(),
                "source_id", saved.getId().toString(),
                "s3_bucket", "ragcraft-source-bucket",
                "s3_key", result.key()
        );

        restTemplate.postForEntity(
                "http://ec2-52-78-41-31.ap-northeast-2.compute.amazonaws.com:8080/process",
                payload,
                Void.class
        );

        // 데이터 처리 시점이나 그 이후에 에러가 발생하는 경우
        // 처리 서버: source_id=12 처리 완료
        // DB: source_id=12 없음
        // 위와 같은 문제가 발생할 수 있기에 해당 부분은 이후에 수정 대상임

        return saved;
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
