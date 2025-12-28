package lab.context.ragcraft.domain.user;

import jakarta.persistence.*;
import lab.context.ragcraft.domain.custommodel.CustomModel;
import lab.context.ragcraft.domain.source.Source;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    private List<Source> sources = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<CustomModel> customModels = new ArrayList<>();

    protected User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }
}