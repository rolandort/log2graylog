package org.rolandort.sender;

import org.rolandort.model.GelfMessage;

import java.util.List;

public interface GelfSender {

  boolean sendMessage(GelfMessage gelfMessage);

  int sendMessages(List<GelfMessage> gelfMessages);
}
