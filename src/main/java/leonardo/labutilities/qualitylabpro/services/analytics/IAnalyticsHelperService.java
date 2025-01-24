package leonardo.labutilities.qualitylabpro.services.analytics;

import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsRecord;
import leonardo.labutilities.qualitylabpro.dtos.analytics.GroupedMeanAndStdRecordByLevel;
import leonardo.labutilities.qualitylabpro.dtos.analytics.GroupedValuesByLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface IAnalyticsHelperService {

    List<GroupedValuesByLevel> findGroupedAnalyticsByLevel(String name, LocalDateTime startDate,
                                                           LocalDateTime endDate);

    List<GroupedMeanAndStdRecordByLevel> returnMeanAndStandardDeviationForGroups(
            List<GroupedValuesByLevel> records);

    Page<AnalyticsRecord> findAnalyticsByNameInAndDateBetweenWithLinks(List<String> names, LocalDateTime dateStart,
                                                                       LocalDateTime dateEnd, Pageable pageable);

    List<GroupedValuesByLevel> findFilteredGroupedAnalytics(List<GroupedValuesByLevel> records);

    void updateAnalyticsMeanByNameAndLevelAndLevelLot(String name, String level, String levelLot,
                                                      double mean);

    boolean isGroupedRecordValid(GroupedValuesByLevel record);

    boolean isRecordValid(AnalyticsRecord record);

    AnalyticsRecord findOneById(Long id);

    void saveNewAnalyticsRecords(List<AnalyticsRecord> valuesOfLevelsList);

    Page<AnalyticsRecord> findAnalytics(Pageable pageable);

    List<AnalyticsRecord> findAnalyticsByNameWithPagination(Pageable pageable, String name);

    Page<AnalyticsRecord> findAnalyticsByNameInByLevelBaseMethod(List<String> names, String level,
                                                                 LocalDateTime startDate, LocalDateTime endDate,
                                                                 Pageable pageable);

    List<AnalyticsRecord> findAnalyticsByDate(LocalDateTime dateStart, LocalDateTime dateEnd);

    Page<AnalyticsRecord> findAnalyticsByNameInAndDateBetween(List<String> names,
                                                              LocalDateTime startDate, LocalDateTime endDate,
                                                              Pageable pageable);

    List<AnalyticsRecord> findAnalyticsByNameAndLevel(Pageable pageable, String name,
                                                      String level);

    List<AnalyticsRecord> findAnalyticsByNameAndLevelAndDate(String name, String level,
                                                             LocalDateTime dateStart, LocalDateTime dateEnd);

    void deleteAnalyticsById(Long id);
}
