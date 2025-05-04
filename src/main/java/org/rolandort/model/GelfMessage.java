package org.rolandort.model;

import com.google.gson.JsonObject;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class GelfMessage {
  String version = "1.1"; // fixed version
  String host;            // required(?)
  String shortMessage;
  String fullMessage;
  Double timestamp;       // epoc sec
  Integer level = 1;      // default log level is 1
  Map<String, Object> additionalFields = new HashMap<>();

  /**
   * @return a JSON string representation of the GelfMessage. The format is as
   *     follows:
   *     <pre>
   *     {
   *       "version": "1.1",
   *       "host": "example.com",
   *       "shortMessage": "short message",
   *       "fullMessage": "full message",
   *       "timestamp": 1234567890,
   *       "level": 1,
   *       "_additionalField1": "value1",
   *       "_additionalField2": "value2"
   *     }
   *     </pre>
   */
  @Override
  public String toString() {
    JsonObject jsonObject = new JsonObject();
    
    // Add standard fields
    jsonObject.addProperty("version", version);
    jsonObject.addProperty("host", host);
    jsonObject.addProperty("short_message", shortMessage);
    jsonObject.addProperty("full_message", fullMessage);
    jsonObject.addProperty("timestamp", timestamp);
    jsonObject.addProperty("level", level);
    
    // Add additional fields with "_" prefix
    if (additionalFields != null) {
      for (Map.Entry<String, Object> entry : additionalFields.entrySet()) {
        String key = "_" + entry.getKey();
        Object value = entry.getValue();
        
        if (value instanceof Number) {
          jsonObject.addProperty(key, (Number) value);
        } else if (value instanceof Boolean) {
          jsonObject.addProperty(key, (Boolean) value);
        } else {
          jsonObject.addProperty(key, value.toString());
        }
      }
    }
    
    return jsonObject.toString();
  }
}
