package org.rolandort.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rolandort.formatter.GelfFormatter;
import org.rolandort.model.GelfMessage;
import org.rolandort.model.LogMessage;
import org.rolandort.parser.LogParser;
import org.rolandort.sender.GelfSender;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class LogProcessingService {
  private static final Logger logger = LogManager.getLogger(LogProcessingService.class);

  private final LogParser logParser;
  private final GelfFormatter gelfFormatter;
  private final GelfSender gelfSender;

  // Constructor-based injector
  @Inject
  public LogProcessingService(LogParser logParser, GelfFormatter gelfFormatter, GelfSender gelfSender) {
    this.logParser = logParser;
    this.gelfFormatter = gelfFormatter;
    this.gelfSender = gelfSender;
  }

  /**
   * Processes a log file, extracts log messages, converts them to GELF format,
   * and sends them to a Graylog server.
   *
   * @param filePath The path to the log file to process.
   * @return The number of log messages that were successfully sent to Graylog.
   */
  public int processLogFile(Path filePath) {

    // Parse log messages from file
    final List<LogMessage> logMessages = logParser.parseLogFile(filePath);
    logger.info("Found {} log messages in file {}", logMessages.size(), filePath);

    if (logMessages.isEmpty()) {
      logger.warn("No log messages found in file {}", filePath);
      return 0;
    }

    // Convert a log message into GELF format and send to Graylog
    final List<GelfMessage> gelfMessages = new ArrayList<>();
    for (LogMessage logMessage : logMessages) {
      GelfMessage gelfMessage = gelfFormatter.formatMessage(logMessage);
      gelfMessages.add(gelfMessage);
    }
    logger.info("Converted {} of {} log messages to GELF format", gelfMessages.size(), logMessages.size());

    // Send GELF messages to Graylog server
    int sentCount = gelfSender.sendMessages(gelfMessages);
    logger.info("Successfully sent {} of {} messages to Graylog", sentCount, logMessages.size());
    return sentCount;
  }
}
