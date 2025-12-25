package lab.context.ragcraft.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3FileStorage implements FileStorage {

    private final S3Client s3Client;

    @Value("${app.s3.bucket}")
    private String bucket;

    @Override
    public UploadResult upload(MultipartFile file, String keyPrefix) {

        String originalName = file.getOriginalFilename() == null
                ? "file"
                : URLEncoder.encode(file.getOriginalFilename(), StandardCharsets.UTF_8);

        String key =
                keyPrefix + "/" +
                        LocalDate.now() + "/" +
                        UUID.randomUUID() + "-" + originalName;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

        } catch (Exception e) {
            throw new IllegalStateException("S3_UPLOAD_FAILED", e);
        }

        String url = "https://" + bucket + ".s3.amazonaws.com/" + key;

        return new UploadResult(key, url);
    }
}