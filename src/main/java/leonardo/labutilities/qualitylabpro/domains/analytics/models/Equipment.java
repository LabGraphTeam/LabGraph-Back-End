package leonardo.labutilities.qualitylabpro.domains.analytics.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "equipments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "commercial_name", nullable = false, length = 100)
    private String commercialName;

    @Column(name = "work_sector", nullable = false, length = 50)
    private String workSector;

    @Column(name = "serial_number", length = 150)
    private String serialNumber;
}
