package leonardo.labutilities.qualitylabpro.configs.rest;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import leonardo.labutilities.qualitylabpro.configs.pagination.CustomPageableResolver;
import leonardo.labutilities.qualitylabpro.domains.shared.components.StringToLocalDateTimeConverter;

@Configuration
@EnableSpringDataWebSupport
public class WebConfig implements WebMvcConfigurer {

	private final StringToLocalDateTimeConverter dateTimeConverter;

	public WebConfig(StringToLocalDateTimeConverter dateTimeConverter) {
		this.dateTimeConverter = dateTimeConverter;
	}

	@Override
	public void addFormatters(@NonNull FormatterRegistry registry) {
		registry.addConverter(this.dateTimeConverter);
	}

	@Override
	public void addArgumentResolvers(
			@NonNull List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new CustomPageableResolver());
	}
}
