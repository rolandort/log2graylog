package org.rolandort.parser;

import org.rolandort.model.LogMessage;

import java.nio.file.Path;
import java.util.List;

public interface LogParser {
  List<LogMessage> parseLogFile(Path filePath);

  LogMessage parseLine(String logLine);
}
