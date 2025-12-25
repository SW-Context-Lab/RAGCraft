package lab.context.ragcraft.domain.source;

import lab.context.ragcraft.domain.user.User;
import lab.context.ragcraft.domain.user.UserRepository;
import lab.context.ragcraft.storage.FileStorage;
import lab.context.ragcraft.storage.UploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
}
