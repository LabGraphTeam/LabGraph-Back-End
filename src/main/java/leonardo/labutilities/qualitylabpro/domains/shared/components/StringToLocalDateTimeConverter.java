package leonardo.labutilities.qualitylabpro.domains.shared.components;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

	private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
			DateTimeFormatter.ISO_DATE_TIME,
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
			DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
			DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
			DateTimeFormatter.ofPattern("yyyy-MM-dd"),
			DateTimeFormatter.ofPattern("dd/MM/yyyy"));

	@Override
	public LocalDateTime convert(@NonNull String source) {
		String sanitizedSource = sanitizeDate(source);

		if (sanitizedSource.isEmpty()) {
			return null;
		}

		DateTimeParseException lastException = null;

		for (DateTimeFormatter formatter : DATE_FORMATTERS) {
			try {
				if (sanitizedSource.length() <= 10) {
					LocalDate date = LocalDate.parse(sanitizedSource, formatter);
					return LocalDateTime.of(date, LocalTime.MIDNIGHT);
				} else {
					return LocalDateTime.parse(sanitizedSource, formatter);
				}
			} catch (DateTimeParseException e) {
				lastException = e;
			}
		}

		throw new IllegalArgumentException("Unable to parse date: " + sanitizedSource, lastException);
	}

	private static String sanitizeDate(String date) {
		return date.trim()
				.replace("--", "-")
				.replaceAll("\\s+", " ")
				.replaceAll("T\\s", "T")
				.replaceAll("\\s(\\d{1,2}:\\d{2})", "T$1")
				.replaceAll("(\\d{4}-\\d{2}-\\d{2})\\s(00:00:00)", "$1T00:00:00");
	}
}
