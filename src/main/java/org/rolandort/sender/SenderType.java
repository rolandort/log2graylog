package org.rolandort.sender;

public enum SenderType {
  HTTP,
  SIMULATE;

  public static SenderType fromString(final String value) {
    if (value == null) {
      return HTTP; // default value
    }

    try {
      return valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      return HTTP; // default value
    }
  }
}
