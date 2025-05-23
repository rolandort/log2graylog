package org.rolandort.formatter;

import com.google.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rolandort.model.GelfMessage;
import org.rolandort.model.LogMessage;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


@Singleton
public class DefaultGelfFormatter implements GelfFormatter {
  private static final Logger logger = LogManager.getLogger(DefaultGelfFormatter.class);

  /**
   * Formats a {@link LogMessage} into a {@link GelfMessage}.
   *
   * @param logMessage the log message to be formatted
   * @return the formatted GELF message
   */
  @Override
  public GelfMessage formatMessage(final LogMessage logMessage) {
    logger.info("Formatting GELF message: {}", logMessage);

    final GelfMessage gelfMessage = new GelfMessage();
    gelfMessage.setHost(logMessage.getClientIp());

    final String short_message = "Request from " + logMessage.getClientIp() + " -> " + logMessage.getDestinationIp() + ": " + logMessage.getClientRequestUri() + " (" + logMessage.getClientStatus() + ")";
    gelfMessage.setShortMessage(short_message);

    final String full_message = "Request from " + logMessage.getClientDeviceType() + " from " + logMessage.getClientIp() + ":" + logMessage.getClientSrcPort() + " -> " + logMessage.getDestinationIp() + ": " + logMessage.getClientRequestUri() + " (" + logMessage.getClientStatus() + ")";
    gelfMessage.setFullMessage(full_message);

    gelfMessage.setTimestamp(logMessage.getEdgeStartTimestamp());
    gelfMessage.setLevel(1); // ToDo: set dynamic log level based on log message

    // Collect all fields from LogMessage with null safety as additional fields
    final Map<String, Object> additionalFields = new HashMap<>();

    // Using for loop to handle null values (not working using steam)
    for (Field field : logMessage.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      try {
        final String fieldName = field.getName();
        final Object fieldValue = field.get(logMessage);
        additionalFields.put(fieldName, fieldValue);
      } catch (IllegalAccessException e) {
        logger.warn("Error accessing null value in field: " + field.getName(), e);
      }
    }

    gelfMessage.setAdditionalFields(additionalFields);
    return gelfMessage;
  }
}
