package leonardo.labutilities.qualitylabpro.repositories;

import jakarta.persistence.QueryHint;
import jakarta.transaction.Transactional;
import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.entities.Analytic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytic, Long> {

	// Existence Checks
	boolean existsByName(String name);

	boolean existsByDateAndLevelAndName(LocalDateTime date, String level, String value);

	// Fetch Analytics by Name
	@Query("SELECT ga FROM generic_analytics ga WHERE ga.name = :name")
	List<Analytic> findByName(@Param("name") String name, Pageable pageable);

	// Fetch Latest Analytics
	@Query(value = """
			SELECT ga FROM generic_analytics ga WHERE ga.name = :name AND ga.level = :level ORDER BY ga.date DESC LIMIT 10
			""")
	List<AnalyticsDTO> findLast10ByNameAndLevel(@Param("name") String name,
			@Param("level") String level);

	@Query(value = """
			SELECT ga FROM generic_analytics ga WHERE ga.name = :name AND ga.level = :level ORDER BY ga.date DESC LIMIT 1
			""")
	List<AnalyticsDTO> findLastByNameAndLevel(@Param("name") String name,
			@Param("level") String level);

	// Update Operations
	@Transactional
	@Modifying
	@Query(value = """
			UPDATE generic_analytics ga SET ga.mean = :mean WHERE
			 ga.name = :name AND ga.level = :level AND ga.levelLot = :levelLot
			""")
	void updateMeanByNameAndLevelAndLevelLot(@Param("name") String name,
			@Param("level") String level, @Param("levelLot") String levelLot,
			@Param("mean") double mean);

	// Fetch Analytics by Name and Level
	@Query(value = """
			SELECT ga FROM generic_analytics ga WHERE ga.name = :name AND ga.level = :level
			""")
	List<Analytic> findByNameAndLevel(Pageable pageable, @Param("name") String name,
			@Param("level") String level);

	@Query(value = """
			SELECT ga FROM generic_analytics ga WHERE ga.name = :name AND ga.level = :level AND ga.levelLot = :levelLot
			""")
	List<Analytic> findByNameAndLevelAndLevelLot(Pageable pageable, @Param("name") String name,
			@Param("level") String level, @Param("levelLot") String levelLot);

	@QueryHints({@QueryHint(name = "org.hibernate.readOnly", value = "true"),
			@QueryHint(name = "org.hibernate.fetchSize", value = "50"),
			@QueryHint(name = "org.hibernate.cacheable", value = "true")})
	@Query("""
			SELECT ga FROM generic_analytics ga WHERE ga.name = :name
			AND ga.level = :level AND ga.date BETWEEN :startDate AND :endDate ORDER BY ga.date ASC
			""")
	List<Analytic> findByNameAndLevelAndDateBetween(@Param("name") String name,
			@Param("level") String level, @Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate, Pageable pageable);

	// Fetch Analytics by Multiple Names and Date

	@QueryHints({@QueryHint(name = "org.hibernate.readOnly", value = "true"),
			@QueryHint(name = "org.hibernate.fetchSize", value = "50"),
			@QueryHint(name = "org.hibernate.cacheable", value = "true")})
	@Query(value = """
			SELECT ga FROM generic_analytics ga WHERE
			 ga.name IN (:names) AND ga.level = :level AND ga.date BETWEEN
			  :startDate AND :endDate
			""")
	Page<AnalyticsDTO> findByNameInAndLevelAndDateBetween(@Param("names") List<String> names,
			@Param("level") String level, @Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate, Pageable pageable);

	@Query(value = """
			SELECT ga FROM generic_analytics ga WHERE ga.name IN (:names) AND ga.date BETWEEN :startDate AND :endDate
			""")
	Page<AnalyticsDTO> findByNameInAndDateBetween(@Param("names") List<String> names,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
			Pageable pageable);

	@Query(value = """
			SELECT ga FROM generic_analytics ga WHERE ga.name IN (:names) AND ga.date BETWEEN :startDate AND :endDate
			""")
	Page<AnalyticsDTO> findByNameInAndDateBetweenPaged(@Param("names") List<String> names,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
			Pageable pageable);

	@Query(value = """
			SELECT ga FROM generic_analytics ga WHERE ga.name IN (:names) ORDER BY ga.date ASC
			""")
	List<Analytic> findByNameIn(@Param("names") List<String> names, Pageable pageable);

	@Query(value = """
			SELECT ga FROM generic_analytics ga WHERE ga.name IN (:names) ORDER BY ga.date ASC
			""")
	Page<AnalyticsDTO> findByNameInPaged(@Param("names") List<String> names, Pageable pageable);

	// General Paged Analytics
	@Query(value = """
			SELECT ga FROM generic_analytics ga ORDER BY ga.date ASC
			""")
	Page<AnalyticsDTO> findPaged(Pageable pageable);

	// Analytics by Date Range
	@Query("SELECT ga FROM generic_analytics ga WHERE ga.date BETWEEN :startDate AND :endDate ORDER BY ga.date DESC")
	List<Analytic> findByDateBetween(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate);

	// Grouped Analytics
	@Query("""
			SELECT ga FROM generic_analytics ga WHERE ga.name = :name
			AND ga.date BETWEEN :startDate AND :endDate GROUP BY ga.level, ga.id ORDER BY ga.date ASC
			""")
	List<Analytic> findByNameAndDateBetweenGroupByLevel(@Param("name") String name,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
			Pageable pageable);
}
