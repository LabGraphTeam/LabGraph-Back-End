package leonardo.labutilities.qualitylabpro.domains.analytics.models;

import org.springframework.beans.BeanUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.EquipmentDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.enums.WorkSectorEnum;
import leonardo.labutilities.qualitylabpro.domains.shared.mappers.EquipmentMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing laboratory equipment.
 */
@Getter
@Setter
@EqualsAndHashCode
@Entity(name = "equipments")
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

        /**
         */
        public Equipment() {}

        public Equipment(EquipmentDTO equipmentDTO) {
                Equipment mappedEquipment = EquipmentMapper.toEntity(equipmentDTO);
                BeanUtils.copyProperties(mappedEquipment, this);
        }
}
