package org.rolandort.parser;

import com.google.inject.Singleton;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rolandort.model.LogMessage;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV log parser implementation
 */
@Singleton
public class CsvLogParser implements LogParser {
  private static final Logger logger = LogManager.getLogger(CsvLogParser.class);
  private final CSVParser parser;

  public CsvLogParser() {
    this.parser = new CSVParserBuilder()
        .withSeparator(',')
        .withQuoteChar('"')
        .withIgnoreLeadingWhiteSpace(true)
        .build();
  }

  /**
   * Parse a CSV log file and extract log messages.
   *
   * @param filePath The path to the log file.
   * @return A list of log messages parsed from the file.
   */
  @Override
  public List<LogMessage> parseLogFile(final Path filePath) {
    logger.info("Parsing CSV log file {}", filePath);
    final List<LogMessage> logMessages = new ArrayList<>();

    try (CSVReader reader = new CSVReaderBuilder(new FileReader(filePath.toFile()))
        .withCSVParser(parser)
        .withSkipLines(1) // Skip header row
        .build()) {
      
      String[] line;
      while ((line = reader.readNext()) != null) {
        final LogMessage message = createLogMessageFromCsvLine(line);
        if (message != null) {
          logMessages.add(message);
        }
      }
      
      logger.info("Successfully parsed {} log messages from CSV file", logMessages.size());
    } catch (IOException | CsvValidationException e) {
      logger.error("Error parsing CSV log file {}", filePath, e);
    }
    
    return logMessages;
  }

  /**
   * Parse a single log line and extract a log message.
   *
   * @param logLine The log line to parse.
   * @return The log message parsed from the log line, or null if the line was empty or invalid.
   */
  @Override
  public LogMessage parseLine(final String logLine) {
    if (logLine == null || logLine.isEmpty()) {
      logger.warn("Empty log line");
      return null;
    }

    try (CSVReader reader = new CSVReaderBuilder(new StringReader(logLine))
        .withCSVParser(parser)
        .build()) {
      
      String[] line = reader.readNext();
      if (line != null) {
        return createLogMessageFromCsvLine(line);
      }
    } catch (IOException | CsvValidationException e) {
      logger.error("Error parsing CSV log line: {}", logLine, e);
    }
    
    return null;
  }
  
  /**
   * Creates a LogMessage object from a CSV line array.
   * 
   * @param line Array of CSV values
   * @return LogMessage object populated with values from the CSV line
   */
  private LogMessage createLogMessageFromCsvLine(final String[] line) {
    if (line == null || line.length < 10) {
      logger.warn("CSV line has insufficient columns: {}", line == null ? "null" : line.length);
      return null;
    }
    
    try {
      LogMessage message = new LogMessage();
      // Map CSV columns to LogMessage fields
      int i = 0;
      message.setClientDeviceType(getSafeString(line, i++));
      message.setClientIp(getSafeString(line, i++));
      message.setClientIpClass(getSafeString(line, i++));
      message.setClientStatus(getSafeInteger(line, i++));
      message.setClientRequestBytes(getSafeInteger(line, i++));
      message.setClientRequestReferer(getSafeString(line, i++));
      message.setClientRequestUri(getSafeString(line, i++));
      message.setClientRequestUserAgent(getSafeString(line, i++));
      message.setClientSrcPort(getSafeInteger(line, i++));
      message.setEdgeServerIp(getSafeString(line, i++));
      message.setEdgeStartTimestamp(getSafeDouble(line, i++));
      message.setDestinationIp(getSafeString(line, i++));
      message.setOriginResponseBytes(getSafeInteger(line, i++));
      message.setOriginResponseTime(getSafeInteger(line, i));
      return message;
    } catch (Exception e) {
      logger.error("Error mapping CSV values to LogMessage: {}", e.getMessage());
      return null;
    }
  }
  
  // Helper methods for safe type conversion
  private String getSafeString(final String[] array, final int col) {
    return col < array.length ? array[col] : null;
  }
  
  private Integer getSafeInteger(final String[] array, final int col) {
    if (col >= array.length || array[col] == null || array[col].isEmpty()) {
      return null;
    }
    try {
      return Integer.parseInt(array[col]);
    } catch (NumberFormatException e) {
      return null;
    }
  }
  
  private Double getSafeDouble(final String[] array, final int col) {
    if (col >= array.length || array[col] == null || array[col].isEmpty()) {
      return null;
    }
    try {
      return Double.parseDouble(array[col]);
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
