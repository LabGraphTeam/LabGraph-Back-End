package leonardo.labutilities.qualitylabpro.domains.analytics.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.ControlLotDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.ControlLotService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@SecurityRequirement(name = "bearer-key")
@RequestMapping("/control-lot")
@RestController
public class ControlLotController {
    private final ControlLotService controlLotService;

    public ControlLotController(ControlLotService controlLotService) {
        this.controlLotService = controlLotService;
    }

    @PostMapping()
    public ResponseEntity<ControlLotDTO> postControlLot(@RequestBody ControlLotDTO controlLot) {
        controlLotService.createControlLot(controlLot);

        return ResponseEntity.created(URI.create("/control-lot/")).body(controlLot);
    }

    @GetMapping()
    public ResponseEntity<List<ControlLotDTO>> getControlLot() {
        return ResponseEntity.ok(controlLotService.getControlLots());
    }

}
