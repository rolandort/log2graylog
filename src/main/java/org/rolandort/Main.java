package org.rolandort;

import org.rolandort.cli.Log2GraylogCli;
import picocli.CommandLine;

public class Main {
  public static void main(String[] args) {
    int exitCode = new CommandLine(new Log2GraylogCli()).execute(args);
    System.exit(exitCode);
  }
}