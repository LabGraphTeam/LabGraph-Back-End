package leonardo.labutilities.qualitylabpro.domains.analytics.repositories;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.QueryHint;
import jakarta.transaction.Transactional;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.responses.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.analytics.models.Analytic;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytic, Long> {

	// Existence Checks
	boolean existsByTestName(String name);

	boolean existsByMeasurementDateAndControlLevelAndTestName(LocalDateTime date, String level,
			String value);

	// Fetch Analytics by Name
	@Query("SELECT ga FROM analytics ga WHERE ga.testName = :testName")
	List<Analytic> findByTestName(@Param("testName") String testName, Pageable pageable);

	// Fetch Latest Analytics
	@Query(value = """
			SELECT ga FROM analytics ga WHERE ga.testName = :name AND ga.controlLevel = :level ORDER BY ga.measurementDate DESC LIMIT 10
			""")
	List<AnalyticsDTO> findLast10ByTestNameAndControlLevel(@Param("testName") String name,
			@Param("controlLevel") String level);

	@Query(value = """
			SELECT ga FROM analytics ga WHERE ga.testName = :name AND ga.controlLevel = :level ORDER BY ga.measurementDate DESC LIMIT 1
			""")
	List<AnalyticsDTO> findLastByTestNameAndControlLevel(@Param("name") String name,
			@Param("level") String level);

	// Update Operations
	@Transactional
	@Modifying
	@Query(value = """
			UPDATE analytics ga SET ga.targetMean = :mean WHERE
			 ga.testName = :name AND ga.controlLevel = :level AND ga.controlLevelLot = :levelLot
			""")
	void updateMeanByNameAndLevelAndLevelLot(@Param("name") String name,
			@Param("level") String level, @Param("levelLot") String levelLot,
			@Param("mean") double mean);

	// Fetch Analytics by Name and Level
	@Query(value = """
			SELECT ga FROM analytics ga WHERE ga.testName = :name AND ga.controlLevel = :level
			""")
	List<Analytic> findByNameAndLevel(Pageable pageable, @Param("name") String name,
			@Param("level") String level);

	@Query(value = """
			SELECT ga FROM analytics ga WHERE ga.testName = :name AND ga.controlLevel = :level AND ga.controlLevelLot = :levelLot
			""")
	List<Analytic> findByNameAndLevelAndLevelLot(Pageable pageable, @Param("name") String name,
			@Param("level") String level, @Param("levelLot") String levelLot);

	@QueryHints({@QueryHint(name = "org.hibernate.readOnly", value = "true"),
			@QueryHint(name = "org.hibernate.fetchSize", value = "100"),
			@QueryHint(name = "org.hibernate.cacheable", value = "true")})
	@Query("""
			SELECT ga FROM analytics ga WHERE ga.testName = :name
			AND ga.controlLevel = :level AND ga.measurementDate BETWEEN :startDate AND :endDate ORDER BY ga.measurementDate ASC
			""")
	List<Analytic> findByNameAndLevelAndDateBetween(@Param("name") String name,
			@Param("level") String level, @Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate, Pageable pageable);

	// Fetch Analytics by Multiple Names and Date
	@QueryHints({@QueryHint(name = "org.hibernate.readOnly", value = "true"),
			@QueryHint(name = "org.hibernate.fetchSize", value = "50"),
			@QueryHint(name = "org.hibernate.cacheable", value = "true")})
	@Query(value = """
			SELECT ga FROM analytics ga WHERE
			 ga.testName IN (:names) AND ga.controlLevel = :level AND ga.measurementDate BETWEEN
			  :startDate AND :endDate
			""")
	Page<AnalyticsDTO> findByNameInAndLevelAndDateBetween(@Param("names") List<String> names,
			@Param("level") String level, @Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate, Pageable pageable);

	@Query(value = """
			SELECT ga FROM analytics ga WHERE ga.testName IN (:names) AND ga.measurementDate BETWEEN :startDate AND :endDate
			""")
	Page<AnalyticsDTO> findByNameInAndDateBetweenPaged(@Param("names") List<String> names,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
			Pageable pageable);


	@Query(value = """
			SELECT ga FROM analytics ga WHERE ga.testName IN (:names) ORDER BY ga.measurementDate ASC
			""")
	List<Analytic> findByNameIn(@Param("names") List<String> names, Pageable pageable);

	@Query(value = """
			SELECT ga FROM analytics ga WHERE ga.testName IN (:names) ORDER BY ga.measurementDate ASC
			""")
	Page<AnalyticsDTO> findByNameInPaged(@Param("names") List<String> names, Pageable pageable);

	// General Paged Analytics
	@Query(value = """
			SELECT ga FROM analytics ga ORDER BY ga.measurementDate ASC
			""")
	Page<AnalyticsDTO> findPaged(Pageable pageable);

	// Analytics by Date Range
	@Query("SELECT ga FROM analytics ga WHERE ga.measurementDate BETWEEN :startDate AND :endDate ORDER BY ga.measurementDate DESC")
	List<Analytic> findByDateBetween(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	// Grouped Analytics
	@Query("""
			SELECT ga FROM analytics ga WHERE ga.testName = :name
			AND ga.measurementDate BETWEEN :startDate AND :endDate GROUP BY ga.controlLevel, ga.id ORDER BY ga.measurementDate ASC
			""")
	List<Analytic> findByNameAndDateBetweenGroupByLevel(@Param("name") String name,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
			Pageable pageable);
}
