package org.rolandort.sender;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rolandort.model.GelfMessage;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class SimulateGelfSender implements GelfSender {
  private static final Logger logger = LogManager.getLogger(SimulateGelfSender.class);


  @Inject
  public SimulateGelfSender() {}

  @Override
  public boolean sendMessage(final GelfMessage gelfMessage) {
    logger.info("Simulated GELF message: '{}'", gelfMessage);
    return true;
  }

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
