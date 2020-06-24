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