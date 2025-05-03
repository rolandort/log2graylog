package org.rolandort.formatter;

import com.google.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rolandort.model.GelfMessage;
import org.rolandort.model.LogMessage;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
public class DefaultGelfFormatter implements GelfFormatter {
  private static final Logger logger = LogManager.getLogger(DefaultGelfFormatter.class);


  @Override
  public GelfMessage formatMessage(LogMessage logMessage) {
    logger.info("Formatting log message: {}", logMessage);

    final GelfMessage gelfMessage = new GelfMessage();
    gelfMessage.setHost(logMessage.getClientIp()); // source
    gelfMessage.setShortMessage("Request: " + logMessage.getClientRequestUri());
    gelfMessage.setFullMessage(logMessage.getClientDeviceType() + " requests " + logMessage.getClientRequestUri());
    gelfMessage.setTimestamp((double) System.currentTimeMillis() / 1000); // TODO: use current timestamp of import for testing purposes
    // gelfMessage.setTimestamp(logMessage.getEdgeStartTimestamp()); // using original log timetamp (will not be visible in Graylog)
    gelfMessage.setLevel(1); // TODO: possibly map log level

    // Collect all fields from LogMessage
    Map<String, Object> additionalFields = Arrays.stream(logMessage.getClass().getDeclaredFields())
        .peek(field -> field.setAccessible(true))
        .collect(Collectors.toMap(
            Field::getName,
            field -> {
              try {
                return field.get(logMessage);
              } catch (IllegalAccessException e) {
                return "ERROR: Cannot access";
              }
            }
        ));

    gelfMessage.setAdditionalFields(additionalFields);
    return gelfMessage;
  }
}
