package leonardo.labutilities.qualitylabpro.domains.users.models;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Analytic;
import leonardo.labutilities.qualitylabpro.domains.users.enums.UserRoles;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private String password;

    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "ownerUserId", fetch = FetchType.LAZY)
    @MapKey(name = "id")
    private SortedMap<Long, Analytic> analytics = new TreeMap<>();

    @OneToMany(mappedBy = "validatorUserId", fetch = FetchType.LAZY)
    @MapKey(name = "id")
    private SortedMap<Long, Analytic> validatedAnalytics = new TreeMap<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.EAGER)
    private UserConfig userConfig;

    @Column(name = "user_roles", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private final UserRoles userRoles;

    @Column(name = "enabled")
    private Boolean isEnabled = true;

    @Column(name = "non_locked")
    private Boolean isAccountNonLocked = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User() {
        this.userConfig = new UserConfig();
        this.userConfig.setUser(this);
        this.userRoles = UserRoles.USER;
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.userRoles = UserRoles.USER;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(UserRoles.values()).filter(role -> this.getUserRoles() == role)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole())).toList();
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy2
                ? hibernateProxy2.getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        return this.getId() != null && Objects.equals(this.getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : this.getClass().hashCode();
    }

    @Override
    public String toString() {
        return String.format("User(id=%d, username='%s', email='%s', role=%s)", this.id,
                this.username, this.email, this.userRoles);
    }
}
