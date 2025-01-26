package leonardo.labutilities.qualitylabpro.repositories;

import jakarta.transaction.Transactional;
import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.entities.Analytic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<Analytic, Long> {
    boolean existsByName(String name);

    @Query("SELECT ga FROM generic_analytics ga WHERE ga.name = ?1")
    List<Analytic> findByName(String name, Pageable pageable);

    boolean existsByDateAndLevelAndName(LocalDateTime date, String level, String value);

    @Query(value = "SELECT ga FROM generic_analytics ga WHERE ga.name = :name AND ga.level = :level ORDER BY ga.date DESC LIMIT 10")
    List<AnalyticsDTO> findLast10ByNameAndLevel(@Param("name") String name, @Param("level") String level);

    @Query(value = "SELECT ga FROM generic_analytics ga WHERE ga.name = :name AND ga.level = :level ORDER BY ga.date DESC LIMIT 10")
    List<Analytic> findLast4ByNameAndLevel(@Param("name") String name, @Param("level") String level);

    @Query(value = "SELECT ga FROM generic_analytics ga WHERE ga.name = :name AND ga.level = :level ORDER BY ga.date DESC LIMIT 1")
    List<Analytic> findLastByNameAndLevel(@Param("name") String name, @Param("level") String level);

    @Transactional
    @Modifying
    @Query("UPDATE generic_analytics ga SET ga.mean = ?4 WHERE ga.name = ?1 AND ga.level = ?2 AND ga.levelLot = ?3")
    void updateMeanByNameAndLevelAndLevelLot(String name, String level, String levelLot, double mean);

    @Query("SELECT ga FROM generic_analytics ga WHERE ga.name = ?1 AND ga.level = ?2")
    List<Analytic> findByNameAndLevel(Pageable pageable, String name, String level);

    @Query("SELECT ga FROM generic_analytics ga WHERE ga.name IN (?1) AND ga.level = ?2 AND ga.date BETWEEN ?3 AND ?4")
    Page<AnalyticsDTO> findByNameInAndLevelAndDateBetween(List<String> names, String level,
                                                          LocalDateTime startDate, LocalDateTime endDate,
                                                          Pageable pageable);

    @Query("SELECT ga FROM generic_analytics ga WHERE ga.name = ?1 AND ga.level = ?2 AND ga.levelLot = ?3")
    List<Analytic> findByNameAndLevelAndLevelLot(Pageable pageable, String name, String level, String levelLot);

    @Query("SELECT ga FROM generic_analytics ga WHERE ga.name IN (?1) AND ga.date BETWEEN ?2 AND ?3")
    Page<AnalyticsDTO> findByNameInAndDateBetween(List<String> names,
                                                  LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("SELECT ga FROM generic_analytics ga WHERE ga.name IN (?1) AND ga.date BETWEEN ?2 AND ?3")
    Page<AnalyticsDTO> findByNameInAndDateBetweenPaged(List<String> names,
                                                       LocalDateTime startDate, LocalDateTime endDate,
                                                       Pageable pageable);

    @Query("SELECT ga FROM generic_analytics ga WHERE ga.name IN (?1) ORDER BY ga.date ASC")
    List<Analytic> findByNameIn(List<String> names, Pageable pageable);

    @Query("SELECT ga FROM generic_analytics ga WHERE ga.name IN (?1) ORDER BY ga.date ASC")
    Page<AnalyticsDTO> findByNameInPaged(List<String> names, Pageable pageable);

    @Query("SELECT ga FROM generic_analytics ga ORDER BY ga.date ASC")
    Page<AnalyticsDTO> findPaged(Pageable pageable);

    @Query("SELECT ga FROM generic_analytics ga WHERE ga.name = ?1 AND ga.level = ?2 AND ga.date BETWEEN ?3 AND ?4 " +
           "ORDER BY ga.date ASC")
    List<Analytic> findByNameAndLevelAndDateBetween(String name, String level,
                                                    LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("SELECT ga FROM generic_analytics ga WHERE ga.date BETWEEN ?1 AND ?2 ORDER BY ga.date DESC")
    List<Analytic> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT ga FROM generic_analytics ga WHERE ga.name = ?1 AND ga.date BETWEEN ?2 AND ?3 GROUP BY ga.level, " +
           "ga.id ORDER BY ga.date ASC")
    List<Analytic> findByNameAndDateBetweenGroupByLevel(String name,
                                                        LocalDateTime startDate, LocalDateTime endDate,
                                                        Pageable pageable);
}
