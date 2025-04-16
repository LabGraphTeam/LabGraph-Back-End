package leonardo.labutilities.qualitylabpro.domains.analytics.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.helpers.AnalyticsHelperUtility;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.AnalyticHelperService;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.AnalyticStatisticsService;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.IAnalyticHelperService;
import leonardo.labutilities.qualitylabpro.domains.analytics.services.IAnalyticStatisticsService;

public class AnalyticsHelperController {

	protected final IAnalyticHelperService analyticHelperService;
	protected final IAnalyticStatisticsService analyticsStatisticsService;

	protected AnalyticsHelperController(AnalyticHelperService analyticHelperService,
			AnalyticStatisticsService analyticsStatisticsService) {
		this.analyticHelperService = analyticHelperService;
		this.analyticsStatisticsService = analyticsStatisticsService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<AnalyticsDTO> getAnalyticsById(@PathVariable final Long id) {
		return ResponseEntity.ok(this.analyticHelperService.findOneById(id));
	}

	public ResponseEntity<CollectionModel<EntityModel<AnalyticsDTO>>> getAllAnalyticsWithLinks(List<String> names,
			Pageable pageable) {
		Page<AnalyticsDTO> resultsList = this.analyticHelperService.findAnalyticsPagedByNameIn(names, pageable);

		List<EntityModel<AnalyticsDTO>> entityModels = resultsList.getContent().stream()
				.map(analyticsRecord -> AnalyticsHelperUtility.createEntityModel(analyticsRecord, this)).toList();

		CollectionModel<EntityModel<AnalyticsDTO>> result =
				AnalyticsHelperUtility.addPaginationLinks(CollectionModel.of(entityModels), resultsList, pageable);
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

		List<EntityModel<AnalyticsDTO>> entityModels = analyticsRecordPaged.getContent().stream()
				.map(analyticsRecord -> AnalyticsHelperUtility.createEntityModel(analyticsRecord, this)).toList();

		CollectionModel<EntityModel<AnalyticsDTO>> result = AnalyticsHelperUtility
				.addPaginationLinks(CollectionModel.of(entityModels), analyticsRecordPaged, pageable);

		return ResponseEntity.ok(result);
	}
}
