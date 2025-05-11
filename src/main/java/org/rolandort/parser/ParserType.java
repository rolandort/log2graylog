package org.rolandort.parser;

public enum ParserType {
  JSON,
  CSV;

  public static ParserType fromString(final String value) {
    if (value == null) {
      return JSON; // default value
    }

    try {
      return valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      return JSON; // default value
    }
  }
}
