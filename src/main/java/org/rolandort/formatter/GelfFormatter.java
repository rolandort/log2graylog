package org.rolandort.formatter;

import org.rolandort.model.GelfMessage;
import org.rolandort.model.LogMessage;

public interface GelfFormatter {
  /**
   * Formats a {@link LogMessage} into a {@link GelfMessage}.
   *
   * @param logMessage the log message to be formatted
   * @return the formatted GELF message
   */
  GelfMessage formatMessage(LogMessage logMessage);
}
