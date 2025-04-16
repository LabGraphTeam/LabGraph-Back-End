package leonardo.labutilities.qualitylabpro.domains.analytic.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.EquipmentDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.enums.WorkSectorEnum;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Equipment;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.EquipmentRepository;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.EquipmentService;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling.ResourceNotFoundException;
import leonardo.labutilities.qualitylabpro.domains.shared.mappers.EquipmentMapper;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTests {

    @Mock
    private EquipmentRepository equipmentRepository;

    private EquipmentService equipmentService;

    private Equipment testEquipment;
    private EquipmentDTO testEquipmentDTO;

    @BeforeEach
    void setUp() {
        this.equipmentService = new EquipmentService(equipmentRepository);

        // Create test data
        testEquipment = new Equipment();
        testEquipment.setId(1);
        testEquipment.setCommercialName("Test Equipment");
        testEquipment.setWorkSector(WorkSectorEnum.BIOCHEMISTRY);
        testEquipment.setSerialNumber("SN12345");

        testEquipmentDTO = new EquipmentDTO(
                "Test Equipment",
                WorkSectorEnum.BIOCHEMISTRY,
                "SN12345",
                null);
    }

    @Test
    @DisplayName("Should find equipment by ID when it exists")
    void findById_ShouldReturnEquipment_WhenEquipmentExists() {
        // Arrange
        when(equipmentRepository.findById(1)).thenReturn(Optional.of(testEquipment));

        // Act
        Equipment result = equipmentService.findById(1);

        // Assert
        assertNotNull(result);
        assertEquals(testEquipment.getId(), result.getId());
        assertEquals(testEquipment.getCommercialName(), result.getCommercialName());
        assertEquals(testEquipment.getWorkSector(), result.getWorkSector());
        assertEquals(testEquipment.getSerialNumber(), result.getSerialNumber());

        // Verify repository was called
        verify(equipmentRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when finding equipment by non-existent ID")
    void findById_ShouldThrowException_WhenEquipmentDoesNotExist() {
        // Arrange
        when(equipmentRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            equipmentService.findById(999);
        });

        // Verify repository was called
        verify(equipmentRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Should return all equipment")
    void findAll_ShouldReturnAllEquipments() {
        // Arrange
        Equipment equipment2 = new Equipment();
        equipment2.setId(2);
        equipment2.setCommercialName("Another Equipment");
        equipment2.setWorkSector(WorkSectorEnum.HEMATOLOGY);
        equipment2.setSerialNumber("SN67890");

        List<Equipment> expectedEquipments = Arrays.asList(testEquipment, equipment2);

        when(equipmentRepository.findAll()).thenReturn(expectedEquipments);

        // Act
        List<Equipment> actualEquipments = equipmentService.findAll();

        // Assert
        assertEquals(expectedEquipments.size(), actualEquipments.size());
        assertEquals(expectedEquipments, actualEquipments);

        // Verify repository was called
        verify(equipmentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should save a new equipment")
    void saveEquipment_ShouldReturnSavedEquipment() {
        // Arrange
        Equipment mappedEquipment = EquipmentMapper.toEntity(testEquipmentDTO);
        mappedEquipment.setId(1);

        when(equipmentRepository.save(any(Equipment.class))).thenReturn(mappedEquipment);

        // Act
        Equipment result = equipmentService.saveEquipment(testEquipmentDTO);

        // Assert
        assertNotNull(result);
        assertEquals(mappedEquipment.getId(), result.getId());
        assertEquals(mappedEquipment.getCommercialName(), result.getCommercialName());
        assertEquals(mappedEquipment.getWorkSector(), result.getWorkSector());
        assertEquals(mappedEquipment.getSerialNumber(), result.getSerialNumber());

        // Verify repository was called
        verify(equipmentRepository, times(1)).save(any(Equipment.class));
    }

    @Test
    @DisplayName("Should find equipment by work sector when it exists")
    void findByWorkSector_ShouldReturnEquipment_WhenEquipmentExists() {
        // Arrange
        when(equipmentRepository.findByWorkSector(WorkSectorEnum.BIOCHEMISTRY))
                .thenReturn(Optional.of(testEquipment));

        // Act
        Equipment result = equipmentService.findByWorkSector(WorkSectorEnum.BIOCHEMISTRY);

        // Assert
        assertNotNull(result);
        assertEquals(testEquipment.getId(), result.getId());
        assertEquals(testEquipment.getWorkSector(), result.getWorkSector());

        // Verify repository was called
        verify(equipmentRepository, times(1)).findByWorkSector(WorkSectorEnum.BIOCHEMISTRY);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when finding equipment by non-existent work sector")
    void findByWorkSector_ShouldThrowException_WhenEquipmentDoesNotExist() {
        // Arrange
        when(equipmentRepository.findByWorkSector(WorkSectorEnum.PATHOLOGY)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            equipmentService.findByWorkSector(WorkSectorEnum.PATHOLOGY);
        });

        // Verify repository was called
        verify(equipmentRepository, times(1)).findByWorkSector(WorkSectorEnum.PATHOLOGY);
    }

    @Test
    @DisplayName("Should update an existing equipment")
    void updateEquipment_ShouldReturnUpdatedEquipment_WhenEquipmentExists() {
        // Arrange
        Integer id = 1;
        EquipmentDTO updateDTO = new EquipmentDTO(
                "Updated Equipment",
                WorkSectorEnum.COAGULATION,
                "SN-UPDATED",
                null);

        Equipment updatedEquipment = new Equipment();
        updatedEquipment.setId(id);
        updatedEquipment.setCommercialName(updateDTO.commercialName());
        updatedEquipment.setWorkSector(updateDTO.workSector());
        updatedEquipment.setSerialNumber(updateDTO.serialNumber());

        when(equipmentRepository.findById(id)).thenReturn(Optional.of(testEquipment));
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(updatedEquipment);

        // Act
        Equipment result = equipmentService.updateEquipment(id, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(updateDTO.commercialName(), result.getCommercialName());
        assertEquals(updateDTO.workSector(), result.getWorkSector());
        assertEquals(updateDTO.serialNumber(), result.getSerialNumber());

        // Verify repository was called
        verify(equipmentRepository, times(1)).findById(id);
        verify(equipmentRepository, times(1)).save(any(Equipment.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent equipment")
    void updateEquipment_ShouldThrowException_WhenEquipmentDoesNotExist() {
        // Arrange
        Integer id = 999;

        when(equipmentRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            equipmentService.updateEquipment(id, testEquipmentDTO);
        });

        // Verify repository was called
        verify(equipmentRepository, times(1)).findById(id);
    }
}
