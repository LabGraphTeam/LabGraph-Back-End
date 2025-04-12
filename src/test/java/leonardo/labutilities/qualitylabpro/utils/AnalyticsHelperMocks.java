package leonardo.labutilities.qualitylabpro.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import leonardo.labutilities.qualitylabpro.domains.analytics.dtos.requests.AnalyticsDTO;

public class AnalyticsHelperMocks {
        public static AnalyticsDTO createSampleRecord() {
                return new AnalyticsDTO(1L, LocalDateTime.of(2024, 12, 16, 7, 53), "0774693",
                                "608384", "ALB2", "PCCC1", 3.45, 3.35, 0.2, "g/dL",
                                "No rule broken", "Approved according to current Westgard configured rules",
                                "validator.tech", "owner_user");
        }

        public static List<AnalyticsDTO> createComprehensiveRecordList() {
                List<AnalyticsDTO> records = new ArrayList<>();
                LocalDateTime baseDate = LocalDateTime.of(2024, 12, 16, 7, 53);

                // Biochemistry Tests (PCCC1, PCCC2)
                records.add(new AnalyticsDTO(1L, baseDate, "0774693", "608384", "ALB2", "PCCC1",
                                3.45, 3.35, 0.2, "g/dL", "No rule broken",
                                "Approved according to current Westgard configured rules",
                                "validator.tech", "owner_user"));
                records.add(new AnalyticsDTO(2L, baseDate.plusMinutes(5), "0774693", "608384",
                                "ALP2S", "PCCC2", 125.0, 120.0, 5.0, "U/L", "No rule broken",
                                "Approved according to current Westgard configured rules", "validator.tech",
                                "owner_user"));
                records.add(new AnalyticsDTO(3L, baseDate.plusMinutes(10), "0774693", "608384",
                                "ASTL", "PCCC1", 32.5, 30.0, 2.0, "U/L", "+1s",
                                "Approved according to current Westgard configured rules", "validator.tech",
                                "owner_user"));
                records.add(new AnalyticsDTO(4L, baseDate.plusMinutes(15), "0774693", "608384",
                                "BILD2", "PCCC2", 0.8, 0.75, 0.05, "mg/dL", "No rule broken",
                                "Approved according to current Westgard configured rules", "validator.tech",
                                "owner_user"));
                records.add(new AnalyticsDTO(5L, baseDate.plusMinutes(20), "0774693", "608384",
                                "CA2", "PCCC1", 9.2, 9.0, 0.3, "mg/dL", "No rule broken",
                                "Approved according to current Westgard configured rules", "validator.tech",
                                "owner_user"));

                // Hematology Tests (LOW, NORMAL, HIGH)
                records.add(new AnalyticsDTO(6L, baseDate.plusMinutes(25), "0774693", "608384",
                                "WBC", "LOW", 3.5, 3.8, 0.2, "10^3/µL", "-2s", "Failed", "validator.tech",
                                "owner_user"));
                records.add(new AnalyticsDTO(7L, baseDate.plusMinutes(30), "0774693", "608384",
                                "RBC", "NORMAL", 4.8, 4.7, 0.1, "10^6/µL", "No rule broken",
                                "Approved according to current Westgard configured rules", "validator.tech",
                                "owner_user"));
                records.add(new AnalyticsDTO(8L, baseDate.plusMinutes(35), "0774693", "608384",
                                "HGB", "HIGH", 16.8, 16.5, 0.3, "g/dL", "+2s", "Failed", "validator.tech",
                                "owner_user"));
                records.add(new AnalyticsDTO(9L, baseDate.plusMinutes(40), "0774693", "608384",
                                "PLT", "NORMAL", 250.0, 245.0, 10.0, "10^3/µL", "No rule broken",
                                "Approved according to current Westgard configured rules", "validator.tech",
                                "owner_user"));

                // Additional Biochemistry with varied statuses
                records.add(new AnalyticsDTO(10L, baseDate.plusMinutes(45), "0774693", "608384",
                                "CHOL2", "PCCC2", 185.0, 180.0, 5.0, "mg/dL", "+1s",
                                "Approved according to current Westgard configured rules", "validator.tech",
                                "owner_user"));
                records.add(new AnalyticsDTO(11L, baseDate.plusMinutes(50), "0774693", "608384",
                                "GLUC3", "PCCC1", 95.0, 90.0, 3.0, "mg/dL", "No rule broken",
                                "Approved according to current Westgard configured rules", "validator.tech",
                                "owner_user"));

                // Differential counts with percentages
                records.add(new AnalyticsDTO(12L, baseDate.plusMinutes(55), "0774693", "608384",
                                "NEU%", "NORMAL", 62.0, 60.0, 2.0, "%", "No rule broken",
                                "Approved according to current Westgard configured rules", "validator.tech",
                                "owner_user"));
                records.add(new AnalyticsDTO(13L, baseDate.plusHours(1), "0774693", "608384",
                                "LYM%", "HIGH", 45.0, 40.0, 2.0, "%", "+2s", "Failed", "validator.tech", "owner_user"));

                // Electrolytes
                records.add(new AnalyticsDTO(14L, baseDate.plusMinutes(65), "0774693", "608384",
                                "NA-I", "PCCC1", 140.0, 138.0, 2.0, "mmol/L", "No rule broken",
                                "Approved according to current Westgard configured rules", "validator.tech",
                                "owner_user"));
                records.add(new AnalyticsDTO(15L, baseDate.plusMinutes(70), "0774693", "608384",
                                "K-I", "PCCC1", 4.2, 4.0, 0.2, "mmol/L", "No rule broken",
                                "Approved according to current Westgard configured rules", "validator.tech",
                                "owner_user"));

                // Failed biochemistry tests
                records.add(new AnalyticsDTO(16L, baseDate.plusMinutes(75), "0774693", "608384",
                                "CREJ2", "PCCC2", 2.5, 1.8, 0.2, "mg/dL", "+3s",
                                "Failed", "validator.tech", "owner_user"));
                records.add(new AnalyticsDTO(17L, baseDate.plusMinutes(80), "0774693", "608384",
                                "UREL", "PCCC2", 85.0, 65.0, 5.0, "mg/dL", "+3s",
                                "Failed", "validator.tech", "owner_user"));

                return records;
        }

        public static List<AnalyticsDTO> createHematologyRecordList() {
                List<AnalyticsDTO> records = new ArrayList<>();
                LocalDateTime baseDate = LocalDateTime.of(2024, 12, 16, 8, 0);

                // Complete Blood Count
                records.add(new AnalyticsDTO(1L, baseDate, "0774693", "608384", "WBC", "NORMAL",
                                7.5, 7.2, 0.3, "10^3/µL", "No rule broken",
                                "Approved according to current Westgard configured rules",
                                "validator.tech", "owner_user"));
                records.add(new AnalyticsDTO(2L, baseDate, "0774693", "608384", "RBC", "NORMAL",
                                5.2, 5.0, 0.2, "10^6/µL", "No rule broken",
                                "Approved according to current Westgard configured rules",
                                "validator.tech", "owner_user"));
                records.add(new AnalyticsDTO(3L, baseDate, "0774693", "608384", "HGB", "NORMAL",
                                14.5, 14.0, 0.5, "g/dL", "No rule broken",
                                "Approved according to current Westgard configured rules",
                                "validator.tech", "owner_user"));
                records.add(new AnalyticsDTO(4L, baseDate, "0774693", "608384", "HCT", "NORMAL",
                                42.0, 41.0, 1.0, "%", "No rule broken",
                                "Approved according to current Westgard configured rules",
                                "validator.tech", "owner_user"));

                // RBC Indices
                records.add(new AnalyticsDTO(5L, baseDate, "0774693", "608384", "MCV", "NORMAL",
                                88.0, 87.0, 1.0, "fL", "No rule broken",
                                "Approved according to current Westgard configured rules",
                                "validator.tech", "owner_user"));
                records.add(new AnalyticsDTO(6L, baseDate, "0774693", "608384", "MCH", "LOW", 27.0,
                                29.0, 1.0, "pg", "-2s", "Failed", "validator.tech", "owner_user"));
                records.add(new AnalyticsDTO(7L, baseDate, "0774693", "608384", "MCHC", "HIGH",
                                36.0, 34.0, 1.0, "g/dL", "+2s", "Failed", "validator.tech", "owner_user"));

                // Differential Counts
                records.add(new AnalyticsDTO(8L, baseDate, "0774693", "608384", "NEU#", "NORMAL",
                                4.5, 4.3, 0.2, "10^3/µL", "No rule broken",
                                "Approved according to current Westgard configured rules",
                                "validator.tech", "owner_user"));
                records.add(new AnalyticsDTO(9L, baseDate, "0774693", "608384", "LYM#", "HIGH", 3.8,
                                3.0, 0.2, "10^3/µL", "+2s", "Failed", "validator.tech", "owner_user"));

                return records;
        }

        public static Page<AnalyticsDTO> createAnalyticsRecordPage(Pageable pageable) {
                List<AnalyticsDTO> records = createSampleRecordList();
                return new PageImpl<>(records, pageable, records.size());
        }

        public static List<AnalyticsDTO> createSampleRecordList() {
                List<AnalyticsDTO> records = new ArrayList<>();

                // Normal level records
                records.add(new AnalyticsDTO(1L, LocalDateTime.of(2024, 12, 16, 7, 53), "0774693",
                                "608384", "ALB2", "PCCC1", 3.45, 3.35, 0.2, "g/dL",
                                "No rule broken", "Approved according to current Westgard configured rules",
                                "validator.tech", "owner_user"));

                records.add(new AnalyticsDTO(2L, LocalDateTime.of(2024, 12, 16, 7, 53), "0774707",
                                "704991", "ALB2", "PCCC2", 4.85, 4.94, 0.3, "g/dL",
                                "No rule broken", "Approved according to current Westgard configured rules",
                                "validator.tech", "owner_user"));

                // Failed records
                records.add(new AnalyticsDTO(3L, LocalDateTime.of(2024, 12, 16, 8, 30), "0774707",
                                "704991", "ALTL", "PCCC2", 82.84, 110.0, 7.0, "U/L", "-3s",
                                "Failed", "validator.tech", "owner_user"));

                // Different analyte records
                records.add(new AnalyticsDTO(4L, LocalDateTime.of(2024, 12, 16, 7, 54), "0774707",
                                "704991", "AMYL2", "PCCC2", 188.64, 187.0, 11.0, "U/L",
                                "No rule broken", "Approved according to current Westgard configured rules",
                                "validator.tech", "owner_user"));

                return records;
        }

        public static List<AnalyticsDTO> createMultiAnalyteRecordList() {
                List<AnalyticsDTO> records = new ArrayList<>();

                // ALB2 record
                records.add(new AnalyticsDTO(1L, LocalDateTime.of(2024, 12, 16, 7, 53), "0774693",
                                "608384", "ALB2", "PCCC1", 3.45, 3.35, 0.2, "g/dL",
                                "No rule broken", "Approved according to current Westgard configured rules",
                                "validator.tech", "owner_user"));

                // ALTL record
                records.add(new AnalyticsDTO(2L, LocalDateTime.of(2024, 12, 16, 9, 2), "0774693",
                                "608384", "ALTL", "PCCC1", 48.1, 46.8, 2.8, "U/L", "No rule broken",
                                "Approved according to current Westgard configured rules", "validator.tech",
                                "owner_user"));

                // CHOL2 record
                records.add(new AnalyticsDTO(3L, LocalDateTime.of(2024, 12, 16, 7, 56), "0774693",
                                "608384", "CHOL2", "PCCC1", 101.69, 104.0, 5.0, "mg/dL",
                                "No rule broken", "Approved according to current Westgard configured rules",
                                "validator.tech", "owner_user"));

                return records;
        }

        public static List<AnalyticsDTO> createDateRangeRecords() {
                List<AnalyticsDTO> records = new ArrayList<>();
                LocalDateTime baseDate = LocalDateTime.of(2024, 12, 16, 7, 53);

                // Add records across multiple timepoints
                for (int i = 0; i < 5; i++) {
                        records.add(new AnalyticsDTO((long) i + 1, baseDate.plusMinutes(i * 30),
                                        "0774693", "608384", "ALB2", "PCCC1", 3.45, 3.35, 0.2,
                                        "g/dL", "No rule broken",
                                        "Approved according to current Westgard configured rules",
                                        "validator.tech", "owner_user"));
                }

                return records;
        }

        public static AnalyticsDTO createOutOfRangeRecord() {
                return new AnalyticsDTO(1L, LocalDateTime.of(2024, 12, 16, 8, 30), "0774707",
                                "704991", "ALTL", "PCCC2", 82.84, 110.0, 7.0, "U/L", "-3s",
                                "Failed", "validator.tech", "owner_user");
        }
}
