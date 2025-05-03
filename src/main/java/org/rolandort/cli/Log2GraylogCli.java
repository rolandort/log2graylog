package org.rolandort.cli;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rolandort.di.AppInjector;
import org.rolandort.service.LogProcessingService;
import picocli.CommandLine;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Callable;


@CommandLine.Command(
    name = "Log2Graylog",
    mixinStandardHelpOptions = true,
    version = "1.1",
    description = "Parses log messages and sends them to Graylog using the GELF format."
)
public class Log2GraylogCli implements Callable<Integer> {
  private static final Logger logger = LogManager.getLogger(Log2GraylogCli.class);

  @CommandLine.Parameters(
      index = "0",
      description = "The log file to parse",
      paramLabel = "LOG_FILE"
  )
  private File logFile;

  @CommandLine.Option(
      names = {"-o", "--out"},
      description = "The output URL of the Graylog GELF HTTP interface",
      defaultValue = "http://localhost:12202/gelf"
  )
  private String graylogUrl = "http://localhost:12202/gelf"; // Default value

  @CommandLine.Option(
      names = {"-v", "--verbose"},
      description = "Enable verbose output"
  )
  private boolean verbose;

  @Override
  public Integer call() throws Exception {
    logger.info("Started Log2Graylog CLI");

    if (verbose) {
      System.out.println("Logfile: " + logFile.getAbsolutePath());
      System.out.println("Graylog URL: " + graylogUrl);
    }

    // Validate log file
    if (!logFile.exists() || !logFile.isFile() || !logFile.canRead()) {
      System.err.println("Error: Log file does not exist, is not a file or cannot be read: " + logFile.getAbsolutePath());
      return 1;
    }

    try {
      // Set up dependency injection with injector class implementation object
      Injector injector = Guice.createInjector(new AppInjector(graylogUrl));
      LogProcessingService logProcessingService = injector.getInstance(LogProcessingService.class);

      // Process the log file
      final Path logFilePath = logFile.toPath();
      logger.info("Processing log file: {}", logFilePath);
      int sentCount = logProcessingService.processLogFile(logFilePath);

      logger.info("Successfully sent {} messages to Graylog", sentCount);
      System.out.println("Successfully sent " + sentCount + " messages to Graylog");
      return 0;
    } catch (Exception e) {
      logger.error("Error processing log file", e);
      System.err.println("Error processing lof file: " + e.getMessage());
      if (verbose) {
        e.printStackTrace();
      }
      return 1;
    }
  }
}
