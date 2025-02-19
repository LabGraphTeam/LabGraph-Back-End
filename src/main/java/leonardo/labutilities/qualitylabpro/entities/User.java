package leonardo.labutilities.qualitylabpro.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import leonardo.labutilities.qualitylabpro.enums.UserRoles;
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

    @OneToMany(mappedBy = "user")
    private List<Analytic> analytics = new ArrayList<>();

    private String username;

    @Setter(AccessLevel.NONE)
    private String password;

    private String email;

    @Column(name = "user_roles")
    private final UserRoles userRoles;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "validatedBy")
    private List<Analytic> validatedAnalytics = new ArrayList<>();

    public User() {
        this.userRoles = UserRoles.USER;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.userRoles = UserRoles.USER;
    }

    public User(String username, String password, String email, UserRoles roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.userRoles = roles;
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
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
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
        User user = (User) o;
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
