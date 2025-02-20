package leonardo.labutilities.qualitylabpro.domains.users.models;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import leonardo.labutilities.qualitylabpro.domains.users.enums.ChartType;
import leonardo.labutilities.qualitylabpro.domains.users.enums.QualityControlRulesApplied;
import leonardo.labutilities.qualitylabpro.domains.users.enums.Theme;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "user_configs")
public class UserConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "default_chart_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ChartType defaultChartType = ChartType.SINGLE_LINE;

    @Column(name = "default_rules", nullable = false)
    @Enumerated(EnumType.STRING)
    private QualityControlRulesApplied defaultRules = QualityControlRulesApplied.RULE_1_3S;

    @Column(name = "theme_preference", nullable = false)
    @Enumerated(EnumType.STRING)
    private Theme themePreference = Theme.LIGHT;

    @Column(name = "auto_calculate_sd", nullable = false)
    private Boolean autoCalculateSd = false;

    @Column(name = "decimal_places", nullable = false)
    private Integer decimalPlaces = 2;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
