package org.rolandort.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rolandort.model.LogMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JSON log parser implementation.
 */
@Singleton
public class JsonLogParser implements LogParser {
  private static final Logger logger = LogManager.getLogger(JsonLogParser.class);
  private final Gson gson;
  
  public JsonLogParser() {
    // Create a Gson instance with custom settings
    this.gson = new GsonBuilder()
        .setLenient() // accept malformed JSON
        .create();
  }

  /**
   * Parse a log file and extract log messages.
   *
   * @param filePath The path to the log file to parse.
   * @return A list of log messages parsed from the file.
   */
  @Override
  public List<LogMessage> parseLogFile(final Path filePath) {
    logger.info("Parsing JSON log file {}", filePath);
    List<LogMessage> logMessages = new ArrayList<>();

    try (Stream<String> lines = Files.lines(Paths.get(filePath.toString()))) {
      // Process each line
      logMessages = lines
          .map(this::parseLine)
          .filter(Objects::nonNull) // ignore empty rows
          .collect(Collectors.toList());
    } catch (IOException e) {
      logger.error("Error parsing JSON log file {}", filePath, e);
    }
    return logMessages;
  }

  /**
   * Parse a single log line and extract a log message as JSON.
   *
   * @param logLine The log line to parse.
   * @return The log message parsed from the log line as JSON, or null if the line was empty or invalid.
   */
  @Override
  public LogMessage parseLine(final String logLine) {

    if (logLine == null || logLine.isEmpty()) {
      logger.warn("Empty log line");
      return null;
    }

    try {
      final LogMessage logMessage = gson.fromJson(logLine, LogMessage.class);
      logger.debug("Parsed log message: {}", logMessage);
      return logMessage;
    } catch (Exception e) {
      logger.error("Error parsing log line: {}", logLine, e);
      return null;
    }
  }
}
