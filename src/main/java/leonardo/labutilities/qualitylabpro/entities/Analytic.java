package leonardo.labutilities.qualitylabpro.entities;

import java.time.LocalDateTime;
import org.springframework.hateoas.RepresentationModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import leonardo.labutilities.qualitylabpro.dtos.analytics.AnalyticsDTO;
import leonardo.labutilities.qualitylabpro.utils.mappers.AnalyticMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity(name = "generic_analytics")
public class Analytic extends RepresentationModel<Analytic> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
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

	public Analytic() {}

	public Analytic(AnalyticsDTO values) {
		AnalyticMapper.toNewEntity(values);
	}

}
