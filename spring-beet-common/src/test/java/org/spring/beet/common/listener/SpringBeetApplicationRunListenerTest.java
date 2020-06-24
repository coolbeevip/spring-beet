package org.spring.beet.common.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest
public class SpringBeetApplicationRunListenerTest {

  @Autowired
  private Environment env;

  @Test
  public void defaultPropertiesTest() {
    assertEquals(env.getProperty("spring.application.name"), "spring.beet-application");
  }
}