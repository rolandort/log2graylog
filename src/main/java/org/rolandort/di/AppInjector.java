package org.rolandort.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.rolandort.formatter.DefaultGelfFormatter;
import org.rolandort.formatter.GelfFormatter;
import org.rolandort.parser.DefaultLogParser;
import org.rolandort.parser.LogParser;
import org.rolandort.sender.GelfSender;
import org.rolandort.sender.HttpGelfSender;

/**
 * Guice module for dependency injection.
 */
public class AppInjector extends AbstractModule {

  private final String graylogUrl;

  public AppInjector(String graylogUrl) {
    this.graylogUrl = graylogUrl;
  }

  // Simple bindings for LogParser and GelfFormatter
  @Override
  protected void configure() {
    // Bind the service to implementation class
    bind(LogParser.class).to(DefaultLogParser.class);
    bind(GelfFormatter.class).to(DefaultGelfFormatter.class);
  }

  // Provider method for GelfSender which requires the graylogUrl
  @Provides
  @Singleton
  public GelfSender provideGelfSender() {
    return new HttpGelfSender(graylogUrl);
  }
}