package leonardo.labutilities.qualitylabpro.domains.analytics.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.beans.BeanUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.EquipmentDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.enums.WorkSectorEnum;
import leonardo.labutilities.qualitylabpro.domains.shared.mappers.EquipmentMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "equipments")
@Getter
@Setter
@EqualsAndHashCode
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
        @JsonIgnore
        @OneToMany(mappedBy = "equipmentId", fetch = FetchType.EAGER)
        private List<ControlLot> controlLots;

        public Equipment() {}

        public Equipment(EquipmentDTO equipmentDTO) {
                Equipment mappedEquipment = EquipmentMapper.toEntity(equipmentDTO);
                BeanUtils.copyProperties(mappedEquipment, this);
        }
}
