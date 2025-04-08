package leonardo.labutilities.qualitylabpro.domains.analytics.models;

import jakarta.persistence.*;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "control_lots")
public class ControlLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "lot_code", nullable = false, length = 50)
    private String lotCode;

    @Column(name = "manufacture_date", nullable = false)
    private LocalDate manufactureDate;

    @Column(name = "expiration_time", nullable = false)
    private LocalDate expirationTime;

    public ControlLot() {}

    public ControlLot(User user, String lotCode, LocalDate manufactureDate, LocalDate expirationTime) {
        this.user = user;
        this.lotCode = lotCode;
        this.manufactureDate = manufactureDate;
        this.expirationTime = expirationTime;
    }
}
