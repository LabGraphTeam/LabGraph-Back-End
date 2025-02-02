package leonardo.labutilities.qualitylabpro.utils.components;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

@Component
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

	private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
			DateTimeFormatter.ISO_DATE_TIME, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
			DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
			DateTimeFormatter.ofPattern("yyyy-MM-dd"), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

	@Override
	public LocalDateTime convert(String source) {
		if (source == null || source.trim().isEmpty()) {
			return null;
		}

		// Sanitize input
		source = sanitizeDate(source);

		DateTimeParseException lastException = null;

		for (DateTimeFormatter formatter : DATE_FORMATTERS) {
			try {
				if (source.length() <= 10) {
					LocalDate date = LocalDate.parse(source, formatter);
					return LocalDateTime.of(date, LocalTime.MIDNIGHT);
				} else {
					return LocalDateTime.parse(source, formatter);
				}
			} catch (DateTimeParseException e) {
				lastException = e;
			}
		}

		throw new IllegalArgumentException("Unable to parse date: " + source, lastException);
	}

	private static String sanitizeDate(String date) {
		return date.trim().replaceAll("--", "-") // Fix double dashes
				.replaceAll("\\s+", " ") // Fix multiple spaces
				.replaceAll("T\\s", "T") // Fix space after T
				.replaceAll("\\s(\\d:\\d)", "T$1") // Add T between date and time if missing
				.replaceAll("(\\d)(\\s00:00:00)", "$1T00:00:00"); // Standardize midnight time
																	// format
	}
}
