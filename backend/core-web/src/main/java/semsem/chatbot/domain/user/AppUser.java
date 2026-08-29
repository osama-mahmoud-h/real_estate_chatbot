package semsem.chatbot.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String password;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            foreignKey = @ForeignKey(name = "FK_user_roles_user_id", foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE")
    )
    @Column(name = "role")
    private Set<UserRole> roles;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean accountNonExpired = true;

    @Column(nullable = false)
    private boolean accountNonLocked = true;

    @Column(nullable = false)
    private boolean credentialsNonExpired = true;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant lastLoginAt;

    // OAuth2 fields
//    @Enumerated(EnumType.STRING)
//    @Column(name = "oauth_provider")
//    private OAuthProvider oauthProvider;

//    @Column(name = "oauth_provider_id")
//    private String oauthProviderId;

    @Column(name = "email_verified")
    private boolean emailVerified = true;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public void recordLogin() {
        lastLoginAt = Instant.now();
    }
}
