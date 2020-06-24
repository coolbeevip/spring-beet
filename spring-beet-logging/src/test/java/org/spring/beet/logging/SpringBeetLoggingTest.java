/**
 * Copyright © 2020 Lei Zhang (zhanglei@apache.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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