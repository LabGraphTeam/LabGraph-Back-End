package leonardo.labutilities.qualitylabpro.domains.analytic.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.ControlLotDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.ControlLot;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Equipment;
import leonardo.labutilities.qualitylabpro.domains.analytics.repositories.ControlLotRepository;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.ControlLotService;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.EquipmentService;
import leonardo.labutilities.qualitylabpro.domains.shared.exception.CustomGlobalErrorHandling.ResourceNotFoundException;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;

@ExtendWith(MockitoExtension.class)
class ControlLotServiceTests {

    @Mock
    private ControlLotRepository controlLotRepository;

    @Mock
    private EquipmentService equipmentService;

    @InjectMocks
    private ControlLotService controlLotService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private User user;
    private Equipment equipment;
    private ControlLotDTO controlLotDTO;
    private ControlLot controlLot;
    private MockedStatic<SecurityContextHolder> mockedSecurityContext;

    @BeforeEach
    void setUp() {
        // Initialize test data
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        equipment = new Equipment();
        equipment.setId(1);

        LocalDate manufactureDate = LocalDate.of(2025, 1, 1);
        LocalDate expirationDate = LocalDate.of(2026, 1, 1);

        controlLotDTO = new ControlLotDTO(
                null,
                null,
                "LOT-123",
                manufactureDate,
                expirationDate,
                equipment.getId());

        controlLot = new ControlLot();
        controlLot.setId(1L);
        controlLot.setUser(user);
        controlLot.setLotCode("LOT-123");
        controlLot.setManufactureDate(manufactureDate);
        controlLot.setExpirationTime(expirationDate);
        controlLot.setEquipmentId(equipment);

        // Mock the security context
        mockedSecurityContext = Mockito.mockStatic(SecurityContextHolder.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
    }

    @AfterEach
    void tearDown() {
        // Release the static mock to prevent memory leaks
        if (mockedSecurityContext != null) {
            mockedSecurityContext.close();
        }
    }

    @Test
    @DisplayName("Should create control lot when user is authenticated")
    void createControlLot_WithAuthenticatedUser_ShouldCreateControlLot() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(user);
        when(equipmentService.findById(controlLotDTO.equipmentId())).thenReturn(equipment);
        when(controlLotRepository.save(any(ControlLot.class))).thenReturn(controlLot);

        // Act
        ControlLot result = controlLotService.createControlLot(controlLotDTO);

        // Assert
        assertNotNull(result);
        assertEquals(controlLotDTO.lotCode(), result.getLotCode());
        assertEquals(controlLotDTO.manufactureDate(), result.getManufactureDate());
        assertEquals(controlLotDTO.expirationTime(), result.getExpirationTime());
        assertEquals(user, result.getUser());

        // Verify interactions
        verify(equipmentService).findById(controlLotDTO.equipmentId());
        verify(controlLotRepository).save(any(ControlLot.class));
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when user is not authenticated")
    void createControlLot_WithUnauthenticatedUser_ShouldThrowException() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> controlLotService.createControlLot(controlLotDTO));

        // Verify no interactions with repositories
        verify(controlLotRepository, never()).save(any(ControlLot.class));
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when authentication is not valid")
    void createControlLot_WithInvalidAuthentication_ShouldThrowException() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> controlLotService.createControlLot(controlLotDTO));

        // Verify no interactions with repositories
        verify(controlLotRepository, never()).save(any(ControlLot.class));
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when principal is not User")
    void createControlLot_WithNonUserPrincipal_ShouldThrowException() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("notUserObject");

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> controlLotService.createControlLot(controlLotDTO));

        // Verify no interactions with repositories
        verify(controlLotRepository, never()).save(any(ControlLot.class));
    }

    @Test
    @DisplayName("Should return list of control lots when they exist")
    void getControlLots_WhenLotsExist_ShouldReturnList() {
        // Arrange
        List<ControlLot> controlLots = List.of(controlLot);
        when(controlLotRepository.findAll()).thenReturn(controlLots);

        // Act
        List<ControlLotDTO> result = controlLotService.getControlLots();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(controlLot.getLotCode(), result.get(0).lotCode());
        assertEquals(controlLot.getId().intValue(), result.get(0).id());
        assertEquals(controlLot.getUser().getUsername(), result.get(0).createdBy());

        // Verify repository was called
        verify(controlLotRepository).findAll();
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when no control lots exist")
    void getControlLots_WhenNoLotsExist_ShouldThrowException() {
        // Arrange
        when(controlLotRepository.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> controlLotService.getControlLots());

        // Verify repository was called
        verify(controlLotRepository).findAll();
    }
}
