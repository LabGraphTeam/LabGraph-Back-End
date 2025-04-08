package leonardo.labutilities.qualitylabpro.domains.analytics.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.RepresentationModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.domains.shared.mappers.AnalyticMapper;
import leonardo.labutilities.qualitylabpro.domains.users.models.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity(name = "analytics")
public class Analytic extends RepresentationModel<Analytic> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_user_id")
	private User ownerUserId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "validator_user_id")
	private User validatorUserId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "equipment_id")
	private Equipment equipmentId;

	@Column(name = "measurement_date", nullable = false)
	private LocalDateTime measurementDate;

	@Column(name = "control_level_lot", length = 25)
	private String controlLevelLot;

	@Column(name = "reagent_lot", length = 25)
	private String reagentLot;

	@Column(name = "test_name", nullable = false, length = 25)
	private String testName;

	@Column(name = "control_level", length = 25)
	private String controlLevel;

	@Column(name = "measurement_value", nullable = false)
	private double measurementValue;

	@Column(name = "target_mean", nullable = false)
	private double targetMean;

	@Column(name = "standard_deviation", nullable = false)
	private double standardDeviation;

	@Column(nullable = false, name = "measurement_unit", length = 15)
	private String measurementUnit;

	@Column(nullable = false, length = 15)
	private String controlRules;

	@Column(nullable = false, length = 50)
	private String description;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false, nullable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public Analytic() {}

	public Analytic(AnalyticsDTO values) {
		Analytic mapped = AnalyticMapper.toNewEntity(values);
		BeanUtils.copyProperties(mapped, this);

	}

}
