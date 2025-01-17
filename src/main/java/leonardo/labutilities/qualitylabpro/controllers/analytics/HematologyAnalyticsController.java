package leonardo.labutilities.qualitylabpro.controllers.analytics;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import leonardo.labutilities.qualitylabpro.constants.AvailableHematologyAnalytics;
import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsRecord;
import leonardo.labutilities.qualitylabpro.dtos.analytics.DefaultMeanAndStdRecord;
import leonardo.labutilities.qualitylabpro.dtos.analytics.MeanAndStdDeviationRecord;
import leonardo.labutilities.qualitylabpro.services.analytics.HematologyAnalyticsService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@SecurityRequirement(name = "bearer-key")
@RequestMapping("/hematology-analytics")
@RestController()
public class HematologyAnalyticsController extends AnalyticsController {

    private static final List<String> names =
            new AvailableHematologyAnalytics().availableHematologyAnalytics();
    private final HematologyAnalyticsService hematologyAnalyticsService;

    public HematologyAnalyticsController(HematologyAnalyticsService hematologyAnalyticsService) {
        super(hematologyAnalyticsService);
        this.hematologyAnalyticsService = hematologyAnalyticsService;
    }

    @Override
    @GetMapping()
    public ResponseEntity<CollectionModel<EntityModel<AnalyticsRecord>>> getAllAnalytics(
            @PageableDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return this.getAllAnalyticsWithLinks(names, pageable);
    }

    @Override
    @GetMapping("/name-and-level")
    public ResponseEntity<List<AnalyticsRecord>> getAllAnalyticsByNameAndLevel(
            Pageable pageable, @RequestParam() String name, @RequestParam() String level) {
        return ResponseEntity
                .ok(hematologyAnalyticsService.findAnalyticsByNameAndLevel(pageable, name, level));
    }

    @Override
    @GetMapping("/date-range")
    public ResponseEntity<List<AnalyticsRecord>> getAllAnalyticsDateBetween(
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate) {
        List<AnalyticsRecord> resultsList =
                hematologyAnalyticsService.findAnalyticsByNameInAndDateBetween(names, startDate, endDate);
        return ResponseEntity.ok(resultsList);
    }

    @Override
    @GetMapping("/name-and-level-date-range")
    public ResponseEntity<List<AnalyticsRecord>> getAllAnalyticsByNameAndLevelDateRange(
            @RequestParam String name, @RequestParam String level,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate) {
        return ResponseEntity.ok(hematologyAnalyticsService
                .findAnalyticsByNameAndLevelAndDate(name, level, startDate, endDate));
    }

    @Override
    @GetMapping("/level-date-range")
    public ResponseEntity<List<AnalyticsRecord>> getAllAnalyticsByLevelDateRange(
            @RequestParam String level,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate,
            Pageable pageable) {
        return ResponseEntity.ok(hematologyAnalyticsService
                .findAnalyticsByNameInByLevel(names, level, startDate, endDate, pageable));
    }

    @Override
    @GetMapping("/mean-standard-deviation")
    public ResponseEntity<MeanAndStdDeviationRecord> getMeanAndStandardDeviation(
            @RequestParam String name, @RequestParam String level,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate) {
        return ResponseEntity.ok(hematologyAnalyticsService.calculateMeanAndStandardDeviation(
                name, level, startDate, endDate));
    }
}
