package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;

/**
 * Practical 11: End-of-Day Transaction Report Generator using Java NIO DirectoryStream,
 * BufferedReader, and BasicFileAttributes.
 */
public class ReportGenerator {

    public static String generateReport(Path logsDir, Path reportFile) throws IOException {
        if (logsDir == null || !Files.exists(logsDir)) {
            throw new IllegalArgumentException("Logs directory does not exist: " + logsDir);
        }

        long totalDeposits = 0;
        long totalWithdrawals = 0;
        int filesProcessed = 0;
        long totalLinesProcessed = 0;

        StringBuilder details = new StringBuilder();
        details.append("===== END-OF-DAY TRANSACTION AUDIT REPORT =====\n");
        details.append("Logs Directory : ").append(logsDir.toAbsolutePath()).append("\n");
        details.append("-----------------------------------------------\n");
        details.append("Processed Log Files:\n");

        // Walk directory using Files.newDirectoryStream (Practical 11)
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsDir)) {
            for (Path entry : stream) {
                // Supplementary rule: skip files that are empty or do not end with .log
                String fileName = entry.getFileName().toString();
                if (!fileName.endsWith(".log")) {
                    continue;
                }

                BasicFileAttributes attrs = Files.readAttributes(entry, BasicFileAttributes.class);
                if (attrs.size() == 0) {
                    continue;
                }

                filesProcessed++;
                details.append(String.format(" - %s | Size: %d bytes | Modified: %s\n",
                        fileName, attrs.size(), attrs.lastModifiedTime()));

                // Read line by line with BufferedReader (Practical 11)
                try (BufferedReader reader = Files.newBufferedReader(entry, StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty()) {
                            continue;
                        }
                        totalLinesProcessed++;
                        String[] tokens = line.split("\\s+");
                        if (tokens.length >= 3) {
                            String action = tokens[0].toUpperCase();
                            try {
                                long amount = Long.parseLong(tokens[tokens.length - 1]);
                                if (action.startsWith("DEPOSIT")) {
                                    totalDeposits += amount;
                                } else if (action.startsWith("WITHDRAW")) {
                                    totalWithdrawals += amount;
                                }
                            } catch (NumberFormatException ignored) {
                                // Skip unparseable amount lines
                            }
                        }
                    }
                }
            }
        }

        long netChange = totalDeposits - totalWithdrawals;

        details.append("-----------------------------------------------\n");
        details.append("Total Log Files Processed : ").append(filesProcessed).append("\n");
        details.append("Total Log Lines Audited   : ").append(totalLinesProcessed).append("\n");
        details.append("Total Deposits Amount     : Rs. ").append(totalDeposits).append("\n");
        details.append("Total Withdrawals Amount  : Rs. ").append(totalWithdrawals).append("\n");
        details.append("Net Change in Bank Pool   : Rs. ").append(netChange).append("\n");
        details.append("===============================================\n");

        String reportContent = details.toString();

        // Write report file if reportFile is provided
        if (reportFile != null) {
            if (reportFile.getParent() != null) {
                Files.createDirectories(reportFile.getParent());
            }
            Files.write(
                reportFile,
                Collections.singletonList(reportContent),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );
        }

        return reportContent;
    }
}
