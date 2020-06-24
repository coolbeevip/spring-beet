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
      log.error(e.getMessage(), e); // $COVERAGE-IGNORE$
    }
  }
}
