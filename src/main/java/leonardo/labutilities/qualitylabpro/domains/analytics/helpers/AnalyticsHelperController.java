package leonardo.labutilities.qualitylabpro.domains.analytics.helpers;

import java.time.LocalDateTime;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.UpdateAnalyticsMeanDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedMeanAndStdByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedResultsByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.AbstractAnalyticHelperService;

public class AnalyticsHelperController {
	private final AbstractAnalyticHelperService analyticHelperService;

	public AnalyticsHelperController(AbstractAnalyticHelperService analyticHelperService) {
		this.analyticHelperService = analyticHelperService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<AnalyticsDTO> getAnalyticsById(@PathVariable Long id) {
		return ResponseEntity.ok(this.analyticHelperService.findOneById(id));
	}

	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<Void> deleteAnalyticsResultById(@PathVariable Long id) {
		this.analyticHelperService.deleteAnalyticsById(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping
	@Transactional
	public ResponseEntity<List<AnalyticsDTO>> postAnalytics(
			@Valid @RequestBody List<AnalyticsDTO> values) {
		this.analyticHelperService.saveNewAnalyticsRecords(values);
		return ResponseEntity.status(201).build();
	}

	@PatchMapping()
	public ResponseEntity<Void> updateAnalyticsMean(
			@Valid @RequestBody UpdateAnalyticsMeanDTO updateAnalyticsMeanDTO) {
		this.analyticHelperService.updateAnalyticsMeanByNameAndLevelAndLevelLot(
				updateAnalyticsMeanDTO.name(), updateAnalyticsMeanDTO.level(),
				updateAnalyticsMeanDTO.levelLot(), updateAnalyticsMeanDTO.mean());
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/grouped-by-level")
	public ResponseEntity<List<GroupedResultsByLevelDTO>> getGroupedByLevel(
			@RequestParam String name, @RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {
		List<GroupedResultsByLevelDTO> groupedData = this.analyticHelperService
				.findAnalyticsWithGroupedResults(name, startDate, endDate, pageable);
		return ResponseEntity.ok(groupedData);
	}

	@GetMapping("/grouped-by-level/mean-deviation")
	public ResponseEntity<List<GroupedMeanAndStdByLevelDTO>> getMeanAndDeviationGrouped(
			@RequestParam String name, @RequestParam("startDate") LocalDateTime startDate,
			@RequestParam("endDate") LocalDateTime endDate,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {
		List<GroupedMeanAndStdByLevelDTO> groupedData = this.analyticHelperService
				.calculateGroupedMeanAndStandardDeviation(name, startDate, endDate, pageable);
		return ResponseEntity.ok(groupedData);
	}

	public ResponseEntity<CollectionModel<EntityModel<AnalyticsDTO>>> getAllAnalyticsWithLinks(
			List<String> names, @PageableDefault(size = 100) @ParameterObject Pageable pageable) {
		Page<AnalyticsDTO> resultsList =
				this.analyticHelperService.findAnalyticsPagedByNameIn(names, pageable);

		var entityModels =
				resultsList.getContent().stream().map(analyticsRecord -> AnalyticsHelperUtility
						.createEntityModel(analyticsRecord, pageable, this)).toList();

		var result = AnalyticsHelperUtility.addPaginationLinks(CollectionModel.of(entityModels),
				resultsList, pageable);
		return ResponseEntity.ok(result);
	}

	public ResponseEntity<CollectionModel<EntityModel<AnalyticsDTO>>> getAnalyticsByDateBetweenWithLinks(
			List<String> names, LocalDateTime startDate, LocalDateTime endDate,
			@PageableDefault(size = 100) @ParameterObject Pageable pageable) {

		Page<AnalyticsDTO> analyticsRecordPaged = this.analyticHelperService
				.findAnalyticsByNameInAndDateBetweenWithLinks(names, startDate, endDate, pageable);

		if (analyticsRecordPaged == null) {
			return ResponseEntity.noContent().build();
		}

		var entityModels = analyticsRecordPaged.getContent().stream()
				.map(analyticsRecord -> AnalyticsHelperUtility.createEntityModel(analyticsRecord,
						pageable, this))
				.toList();

		var collectionModel = CollectionModel.of(entityModels);
		var result = AnalyticsHelperUtility.addPaginationLinks(collectionModel,
				analyticsRecordPaged, pageable);

		return ResponseEntity.ok(result);
	}
}
