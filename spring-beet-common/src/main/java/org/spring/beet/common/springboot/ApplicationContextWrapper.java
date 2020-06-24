package org.spring.beet.common.springboot;

import java.util.Optional;
import org.springframework.context.ApplicationContext;

public class ApplicationContextWrapper {

  private static Optional<ApplicationContext> context = Optional.empty();

  public static synchronized void setApplicationContext(ApplicationContext applicationContext) {
    ApplicationContextWrapper.context = Optional.of(applicationContext);
  }

  public static Optional<ApplicationContext> getContext() {
    return context;
  }
}