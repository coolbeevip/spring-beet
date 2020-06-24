package org.spring.beet.common.springboot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

/**
 * @author zhanglei
 */
@Slf4j
public class SpringBeetApplicationRunListener implements SpringApplicationRunListener {

  public SpringBeetApplicationRunListener(SpringApplication application, String[] args) {
  }

  @Override
  public void environmentPrepared(ConfigurableEnvironment environment) {
    Properties properties = new Properties();
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      try (InputStream in = classLoader.getResourceAsStream("spring-beet-default.properties")) {
        properties.load(in);
      }
      environment.getPropertySources()
          .addLast(new PropertiesPropertySource("spring-beet-default", properties));
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }
}
