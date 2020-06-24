package org.spring.beet.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = {LoggingAutoConfiguration.class})
public class SensitiveLoggingTest {

  @Test
  public void testMobileNumberValidate() {
    log.info("18610099300");
    assertEquals("186****9300", SensitiveAppender.getLastMessage());
  }
}