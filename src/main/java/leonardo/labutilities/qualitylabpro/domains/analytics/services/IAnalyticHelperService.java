package leonardo.labutilities.qualitylabpro.domains.analytics.services;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsWithCalcDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedResultsByLevelDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.GroupedValuesByLevelDTO;

public interface IAnalyticHelperService {

	String convertLevel(String level);

	List<GroupedResultsByLevelDTO> findAnalyticsWithGroupedResults(String name,
			LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

	Page<AnalyticsDTO> findAnalyticsPagedByNameIn(List<String> names, Pageable pageable);

	List<GroupedValuesByLevelDTO> findGroupedAnalyticsByLevel(String name, LocalDateTime startDate,
			LocalDateTime endDate, Pageable pageable);

	Page<AnalyticsDTO> findAnalyticsByNameInAndDateBetweenWithLinks(List<String> names,
			LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable);

	void updateAnalyticsMeanByNameAndLevelAndLevelLot(String name, String level, String levelLot,
			double mean);

	AnalyticsDTO findOneById(Long id);

	void saveNewAnalyticsRecords(List<AnalyticsDTO> valuesOfLevelsList);

	List<AnalyticsDTO> findAnalyticsByNameWithPagination(List<String> names, String name,
			Pageable pageable);

	Page<AnalyticsDTO> findAnalyticsByNameInByLevel(List<String> names, String level,
			LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

	List<AnalyticsDTO> findAnalyticsByDate(LocalDateTime dateStart, LocalDateTime dateEnd);

	Page<AnalyticsDTO> findAnalyticsByNameInAndDateBetween(List<String> names,
			LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

	List<AnalyticsDTO> findAnalyticsByNameAndLevel(Pageable pageable, String name, String level);

	AnalyticsWithCalcDTO findAnalyticsByNameLevelDate(String name, String level,
			LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable);

	void deleteAnalyticsById(Long id);

	AnalyticsDTO validateAnalyticByUser(Long id);

}
