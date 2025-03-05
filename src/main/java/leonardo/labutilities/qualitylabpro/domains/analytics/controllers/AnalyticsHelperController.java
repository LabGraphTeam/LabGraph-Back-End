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
import leonardo.labutilities.qualitylabpro.domains.analytics.services.IAnalyticHelperService;

public class AnalyticsHelperController {

	protected final IAnalyticHelperService analyticHelperService;

	protected AnalyticsHelperController(AnalyticHelperService analyticHelperService) {
		this.analyticHelperService = analyticHelperService;
	}

	@GetMapping("/{id}")
	public ResponseEntity<AnalyticsDTO> getAnalyticsById(@PathVariable Long id) {
		return ResponseEntity.ok(this.analyticHelperService.findOneById(id));
	}

	public ResponseEntity<CollectionModel<EntityModel<AnalyticsDTO>>> getAllAnalyticsWithLinks(
			List<String> names, @PageableDefault(size = 100) @ParameterObject Pageable pageable) {
		Page<AnalyticsDTO> resultsList =
				this.analyticHelperService.findAnalyticsPagedByNameIn(names, pageable);

		var entityModels = resultsList.getContent().stream().map(
				analyticsRecord -> AnalyticsHelperUtility.createEntityModel(analyticsRecord, this))
				.toList();

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

		var entityModels = analyticsRecordPaged.getContent().stream().map(
				analyticsRecord -> AnalyticsHelperUtility.createEntityModel(analyticsRecord, this))
				.toList();

		var collectionModel = CollectionModel.of(entityModels);
		var result = AnalyticsHelperUtility.addPaginationLinks(collectionModel,
				analyticsRecordPaged, pageable);

		return ResponseEntity.ok(result);
	}
}
