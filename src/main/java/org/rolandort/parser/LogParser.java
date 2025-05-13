package org.rolandort.parser;

import org.rolandort.model.LogMessage;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface LogParser {
  List<LogMessage> parseLogFile(Path filePath);

  Optional<LogMessage> parseLine(String logLine);
}
