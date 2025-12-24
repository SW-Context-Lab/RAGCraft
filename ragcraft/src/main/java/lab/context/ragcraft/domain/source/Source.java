package lab.context.ragcraft.domain.source;

import jakarta.persistence.*;
import lab.context.ragcraft.domain.user.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Source {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false, length = 1024)
    private String s3Key;

    @Column(nullable = false, length = 2048)
    private String s3Url;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Source() {}

    public Source(User user, String originalFilename, String contentType, long size, String s3Key, String s3Url) {
        this.user = user;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.size = size;
        this.s3Key = s3Key;
        this.s3Url = s3Url;
        this.createdAt = LocalDateTime.now();
    }
}
