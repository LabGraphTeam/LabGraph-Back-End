package leonardo.labutilities.qualitylabpro.domains.analytics.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.EquipmentDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.enums.WorkSectorEnum;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Equipment;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.EquipmentService;

@SecurityRequirement(name = "bearer-key")
@RequestMapping("/equipment")
@RestController
public class EquipmentController {
    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipment> getEquipmentById(@PathVariable Integer id) {
        return ResponseEntity.ok(equipmentService.findById(id));
    }

    @GetMapping()
    public ResponseEntity<List<Equipment>> getAllEquipments() {
        return ResponseEntity.ok(equipmentService.findAll());
    }

    @PostMapping()
    public ResponseEntity<Equipment> postEquipment(@RequestBody EquipmentDTO createEquipmentDTO) {
        var response = equipmentService.saveEquipment(createEquipmentDTO);

        return ResponseEntity.created(URI.create("/equipment/")).body(response);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Equipment> patchEquipment(@PathVariable Integer id,
            @RequestBody EquipmentDTO updateEquipmentDTO) {
        return ResponseEntity.ok(equipmentService.updateEquipment(id, updateEquipmentDTO));
    }

    @GetMapping("/work-sector/{workSector}")
    public ResponseEntity<Equipment> getEquipmentByWorkSector(@PathVariable WorkSectorEnum workSector) {
        return ResponseEntity.ok(equipmentService.findByWorkSector(workSector));
    }
}
