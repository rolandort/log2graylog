package org.rolandort.formatter;

import org.rolandort.model.GelfMessage;
import org.rolandort.model.LogMessage;

public interface GelfFormatter {
  GelfMessage formatMessage(LogMessage logMessage);
}
