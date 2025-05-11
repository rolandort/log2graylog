package org.rolandort.cli;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rolandort.di.AppInjector;
import org.rolandort.parser.ParserType;
import org.rolandort.sender.SenderType;
import org.rolandort.service.LogProcessingService;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.Callable;


@CommandLine.Command(
    name = "Log2Graylog",
    mixinStandardHelpOptions = true,
    version = "1.1.0",
    description = "Parses log messages and send them to Graylog using the GELF format."
)
public class Log2GraylogCli implements Callable<Integer> {
  private static final Logger logger = LogManager.getLogger(Log2GraylogCli.class);

  @CommandLine.Parameters(
      index = "0",
      description = "Logfile to parse as input",
      paramLabel = "LOG_FILE"
  )
  private File logFile;

  @CommandLine.Option(
      names = {"-p", "--parser"},
      description = "Parser type (JSON|CSV, default: ${DEFAULT-VALUE})",
      defaultValue = "JSON"
  )
  private ParserType parserType;

  @CommandLine.Option(
      names = {"-s", "--sender"},
      description = "Sender type (SIMULATE|HTTP, default: ${DEFAULT-VALUE})",
      defaultValue = "HTTP"
  )
  private SenderType senderType;

  @CommandLine.Option(
      names = {"-u", "--url"},
      description = "Output URL of the Graylog GELF HTTP interface (default: ${DEFAULT-VALUE})",
      defaultValue = "http://localhost:12202/gelf"
  )
  private String graylogUrl;

  @CommandLine.Option(
      names = {"-t", "--timeout"},
      description = "Timeout of HTTP requests in seconds. (default: ${DEFAULT-VALUE} sec)",
      defaultValue = "10"
  )
  private int timeout;

  @CommandLine.Option(
      names = {"-v", "--verbose"},
      description = "Enable verbose output"
  )
  private boolean verbose;

  /**
   * Log2Graylog CLI application.
   * <p>
   * This method is called when the application is started from the command line.
   * It performs the following steps:
   * <p>
   * The method returns 0 if the log file was successfully processed and sent to Graylog,
   * 1 otherwise.
   */
  @Override
  public Integer call() {
    logger.info("Started Log2Graylog CLI version {}", Log2GraylogCli.class.getPackage().getImplementationVersion());

    if (verbose) {
      System.out.println("Logfile: " + logFile.getAbsolutePath());
      System.out.println("Parser: " + parserType);
      System.out.println("Graylog URL: " + graylogUrl);
      System.out.println("Timeout: " + timeout + " sec");
    }

    // Validate log file
    if (!logFile.exists() || !logFile.isFile() || !logFile.canRead()) {
      System.err.println("Error: Log file does not exist, is not a file or cannot be read: " + logFile.getAbsolutePath());
      return 1;
    }

    try {
      // Set up dependency injection with injector class implementation object
      final Injector injector = Guice.createInjector(new AppInjector(parserType, senderType, graylogUrl, timeout));
      final LogProcessingService logProcessingService = injector.getInstance(LogProcessingService.class);

      // Process the log file
      final Path logFilePath = logFile.toPath();
      int sentCount = logProcessingService.processLogFile(logFilePath);
      logger.info("Successfully sent {} messages to Graylog ({}, {})", sentCount, parserType, senderType);
      System.out.println("Successfully sent " + sentCount + " messages to Graylog");
      return 0;

    } catch (final Exception e) {
      logger.error("Error processing log file", e);
      System.err.println("Error processing log file: " + e.getMessage());
      // The verbose flag controls detailed error output
      if (verbose) {
        logger.debug("Detailed stack trace:", e);
      }
      return 1;
    }
  }
}
