package org.rolandort.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.rolandort.formatter.DefaultGelfFormatter;
import org.rolandort.formatter.GelfFormatter;
import org.rolandort.parser.CsvLogParser;
import org.rolandort.parser.JsonLogParser;
import org.rolandort.parser.LogParser;
import org.rolandort.parser.ParserType;
import org.rolandort.sender.GelfSender;
import org.rolandort.sender.HttpGelfSender;
import org.rolandort.sender.SenderType;
import org.rolandort.sender.SimulateGelfSender;

/**
 * Guice module for dependency injection.
 */
public class AppInjector extends AbstractModule {

  final ParserType parserType;
  final SenderType senderType;
  private final String graylogUrl;
  private final int timeout;

  public AppInjector(final ParserType parserType, final SenderType senderType, final String graylogUrl, final int timeout) {
    this.parserType = parserType;
    this.senderType = senderType;
    this.graylogUrl = graylogUrl;
    this.timeout = timeout;
  }

  // Simple bindings for GelfFormatter
  @Override
  protected void configure() {
    // Bind the service to implementation class
    bind(GelfFormatter.class).to(DefaultGelfFormatter.class);
  }

  // Provider method for LogParser depending on parser type
  @Provides
  @Singleton
  public LogParser provideLogParser() {
    return switch (parserType) {
      case CSV -> new CsvLogParser();
      case JSON -> new JsonLogParser();
    };
  }

  // Sender method for GelfSender depending on sender type
  @Provides
  @Singleton
  public GelfSender provideGelfSender() {
    return switch (senderType) {
      case HTTP -> new HttpGelfSender(graylogUrl, timeout);
      case SIMULATE -> new SimulateGelfSender();
    };
  }
}