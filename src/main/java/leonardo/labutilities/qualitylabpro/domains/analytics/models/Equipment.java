package leonardo.labutilities.qualitylabpro.domains.analytics.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import leonardo.labutilities.qualitylabpro.domains.analytics.enums.WorkSectorEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "equipments")
@Getter
@Setter
@NoArgsConstructor
public class Equipment {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(name = "commercial_name", nullable = false, length = 100)
        private String commercialName;

        @Enumerated(EnumType.STRING)
        @Column(name = "work_sector", nullable = false, length = 50)
        private WorkSectorEnum workSector;

        @Column(name = "serial_number", length = 150)
        private String serialNumber;

        public Equipment(String commercialName, WorkSectorEnum workSector, String serialNumber) {
                this.commercialName = commercialName;
                this.workSector = workSector;
                this.serialNumber = serialNumber;
        }

        @Override
        public boolean equals(Object o) {
                if (this == o)
                        return true;
                if (o == null || getClass() != o.getClass())
                        return false;
                Equipment equipment = (Equipment) o;
                return id != null && id.equals(equipment.id);
        }

        @Override
        public int hashCode() {
                return getClass().hashCode();
        }

        @Override
        public String toString() {
                return "Equipment{" +
                                "id=" + id +
                                ", commercialName='" + commercialName + '\'' +
                                ", workSector=" + workSector +
                                ", serialNumber='" + serialNumber + '\'' +
                                '}';
        }
}
