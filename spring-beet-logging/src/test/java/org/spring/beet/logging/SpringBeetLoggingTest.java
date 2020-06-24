package org.spring.beet.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@Slf4j
@SpringBootTest(classes = {LoggingAutoConfiguration.class})
public class SpringBeetLoggingTest {

  @Autowired
  Environment env;

  @Test
  public void defaultPropertiesTest() {
    assertEquals(env.getProperty("logging.file.path"), "logs");
    assertEquals(env.getProperty("logging.file.name"), "spring.beet.log");
    assertEquals(env.getProperty("logging.sensitive[0].regex"), "(\\d{3})\\d{4}(\\d{4})");
    assertEquals(env.getProperty("logging.sensitive[0].replacement"), "$1****$2");
  }

}