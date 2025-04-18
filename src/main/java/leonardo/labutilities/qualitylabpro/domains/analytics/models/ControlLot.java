package leonardo.labutilities.qualitylabpro.domains.analytics.models;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Entity(name = "control_lots")
public class ControlLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user")
    @JsonIgnoreProperties({"analytics", "controlLots"})
    private User user;

    @Column(name = "lot_code", nullable = false, length = 50)
    private String lotCode;

    @Column(name = "manufacture_date", nullable = false)
    private LocalDate manufactureDate;

    @Column(name = "expiration_time", nullable = false)
    private LocalDate expirationTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_equipment")
    private Equipment equipment;

    public ControlLot() {}

    public ControlLot(User user, String lotCode, LocalDate manufactureDate, LocalDate expirationTime) {
        this.user = user;
        this.lotCode = lotCode;
        this.manufactureDate = manufactureDate;
        this.expirationTime = expirationTime;
    }
}
