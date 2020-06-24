package org.spring.beet.common.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.spring.beet.common.springboot.ApplicationContextWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest
public class SpringBeetApplicationRunListenerTest {

  @Autowired
  Environment env;

  @Test
  public void defaultPropertiesTest() {
    assertEquals(env.getProperty("spring.application.name"), "spring.beet-application");
  }

  @Test
  public void applicationContextWrapperTest() {
    assertTrue(ApplicationContextWrapper.getContext().isPresent());
  }
}