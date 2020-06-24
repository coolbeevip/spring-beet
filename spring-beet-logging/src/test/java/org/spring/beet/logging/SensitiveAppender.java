package org.spring.beet.logging;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import java.util.LinkedList;

public class SensitiveAppender extends ConsoleAppender<LoggingEvent> {

  static LinkedList<String> messages = new LinkedList<>();

  @Override
  protected void append(LoggingEvent event) {
    byte[] byteArray = this.encoder.encode(event);
    String message = new String(byteArray);
    messages.add(message);
  }

  public static String getLastMessage() {
    return messages.getLast();
  }
}