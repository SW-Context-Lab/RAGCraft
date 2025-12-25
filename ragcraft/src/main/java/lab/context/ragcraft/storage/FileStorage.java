package lab.context.ragcraft.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {
    UploadResult upload(MultipartFile file, String keyPrefix);
    String generatePresignedUrl(String s3Key, int expireSeconds);
}
