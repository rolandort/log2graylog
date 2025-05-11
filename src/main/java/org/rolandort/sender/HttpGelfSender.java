package org.rolandort.sender;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rolandort.model.GelfMessage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class HttpGelfSender implements GelfSender {
  private static final Logger logger = LogManager.getLogger(HttpGelfSender.class);

  private final String graylogUrl;
  private final int timeout;
  private final HttpClient client;

  @Inject
  public HttpGelfSender(String graylogUrl, int timeout) {
    this.graylogUrl = graylogUrl;
    this.timeout = timeout;
    this.client = HttpClient.newHttpClient();
  }

  /**
   * Sends a GELF message to a Graylog server over HTTP.
   * <p>
   * The message is sent as a POST request to the Graylog server.
   * The method returns true if the message was successfully sent, false otherwise.
   *
   * @param gelfMessage The GELF message to send
   * @return True if the message was successfully sent, false otherwise
   */
  @Override
  public boolean sendMessage(final GelfMessage gelfMessage) {
    logger.info("Sending GELF message to {} (timeout: {} sec): '{}'", graylogUrl, timeout, gelfMessage.toString());

    // HTTP request using internal HttpClient of Java >=17
    final HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(this.graylogUrl))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(gelfMessage.toString()))
        .timeout(java.time.Duration.ofSeconds(timeout))
        .build();

    try {
      // Send the HTTP request
      final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() < 200 ||response.statusCode() >= 300) {
        logger.error("Error {} sending GELF message to {}: '{}'", graylogUrl, response.statusCode(), gelfMessage);
        return false;
      }
    } catch (IOException | InterruptedException e) {
      logger.error("Exception ({}) sending GELF message to {}: '{}'", e.getCause(), graylogUrl, gelfMessage, e);
      return false;
    }

    logger.info("Successfully sent GELF message to {}: '{}'", graylogUrl, gelfMessage);
    return true;
  }

  /**
   * Send a list of GELF messages to the Graylog server over HTTP.
   *
   * @param gelfMessages The list of GELF messages to send
   * @return The number of messages that were sent successfully
   */
  @Override
  public int sendMessages(final List<GelfMessage> gelfMessages) {
    final AtomicInteger sentCount = new AtomicInteger(0);

    for (GelfMessage gelfMessage : gelfMessages) {
      if (this.sendMessage(gelfMessage)) {
        sentCount.incrementAndGet();
      }
    }
    return sentCount.get();
  }
}
