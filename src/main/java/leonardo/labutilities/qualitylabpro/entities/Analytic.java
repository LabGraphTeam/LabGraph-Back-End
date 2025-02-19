package leonardo.labutilities.qualitylabpro.entities;

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
import leonardo.labutilities.qualitylabpro.dtos.analytics.responses.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.utils.mappers.AnalyticMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity(name = "generic_analytics")
public class Analytic extends RepresentationModel<Analytic> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	private LocalDateTime date;

	@Column(name = "level_lot")
	private String levelLot;

	@Column(name = "test_lot")
	private String testLot;

	private String name;

	private String level;

	private double value;

	private double mean;

	private double sd;
	@Column(name = "unit_value")

	private String unitValue;

	private String rules;

	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "validated_by_id")
	private User validatedBy;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
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
