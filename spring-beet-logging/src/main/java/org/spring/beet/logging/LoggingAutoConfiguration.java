package org.spring.beet.logging;

import lombok.extern.slf4j.Slf4j;
import org.spring.beet.common.springboot.ApplicationContextWrapper;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;

@Slf4j
@SuppressWarnings("unchecked")
public class LoggingAutoConfiguration implements ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Bean
  public ApplicationContextWrapper applicationContextWrapper() {
    ApplicationContextWrapper contextWrapper = new ApplicationContextWrapper();
    contextWrapper.setApplicationContext(this.applicationContext);
    return contextWrapper;
  }

  @Bean(name = "loggingSensitiveProperties")
  public LoggingSensitiveProperties loggingSensitiveProperties() {
    LoggingSensitiveProperties loggingSensitiveProperties = Binder
        .get(this.applicationContext.getEnvironment())
        .bind("logging", LoggingSensitiveProperties.class).orElse(null);
    return loggingSensitiveProperties;
  }
}