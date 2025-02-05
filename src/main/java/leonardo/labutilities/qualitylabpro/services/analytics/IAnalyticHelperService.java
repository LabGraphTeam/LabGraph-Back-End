package leonardo.labutilities.qualitylabpro.services.analytics;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.dtos.analytics.GroupedMeanAndStdByLevelDTO;
import leonardo.labutilities.qualitylabpro.dtos.analytics.GroupedValuesByLevelDTO;

public interface IAnalyticHelperService {

	List<GroupedValuesByLevelDTO> findGroupedAnalyticsByLevel(String name, LocalDateTime startDate,
			LocalDateTime endDate, Pageable pageable);

	List<GroupedMeanAndStdByLevelDTO> returnMeanAndStandardDeviationForGroups(
			List<GroupedValuesByLevelDTO> records);

	Page<AnalyticsDTO> findAnalyticsByNameInAndDateBetweenWithLinks(List<String> names,
			LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable);

	List<GroupedValuesByLevelDTO> findFilteredGroupedAnalytics(
			List<GroupedValuesByLevelDTO> records);

	void updateAnalyticsMeanByNameAndLevelAndLevelLot(String name, String level, String levelLot,
			double mean);

	boolean isGroupedRecordValid(GroupedValuesByLevelDTO groupedValuesByLevelDTO);

	boolean isRecordValid(AnalyticsDTO analyticsDTO);

	AnalyticsDTO findOneById(Long id);

	void saveNewAnalyticsRecords(List<AnalyticsDTO> valuesOfLevelsList);

	Page<AnalyticsDTO> findAnalytics(Pageable pageable);

	List<AnalyticsDTO> findAnalyticsByNameWithPagination(Pageable pageable, String name);

	Page<AnalyticsDTO> findAnalyticsByNameInByLevelBaseMethod(List<String> names, String level,
			LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

	List<AnalyticsDTO> findAnalyticsByDate(LocalDateTime dateStart, LocalDateTime dateEnd);

	Page<AnalyticsDTO> findAnalyticsByNameInAndDateBetween(List<String> names,
			LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

	List<AnalyticsDTO> findAnalyticsByNameAndLevel(Pageable pageable, String name, String level);

	List<AnalyticsDTO> findAnalyticsByNameAndLevelAndDate(String name, String level,
			LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable);

	void deleteAnalyticsById(Long id);
}
