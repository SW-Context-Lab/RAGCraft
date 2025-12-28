package lab.context.ragcraft.domain.custommodel;

import jakarta.persistence.*;
import lab.context.ragcraft.domain.source.Source;
import lab.context.ragcraft.domain.user.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class CustomModel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_id", nullable = false)
    private Source source;

    @Column(nullable = false, length = 50)
    private String modelType;

    @Column(nullable = false, length = 255)
    private String displayName;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected CustomModel() {}

    public CustomModel(User user, Source source, String modelType, String displayName, String description) {
        this.user = user;
        this.source = source;
        this.modelType = modelType;
        this.displayName = displayName;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
}
