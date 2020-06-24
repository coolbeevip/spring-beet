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

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.Iterator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.spring.beet.common.springboot.ApplicationContextWrapper;

@Slf4j
public class ConversionAdapter extends MessageConverter {

  @Setter
  private LoggingSensitiveProperties loggingSensitiveProperties;

  @Override
  public String convert(ILoggingEvent event) {
    if (loggingSensitiveProperties == null) {
      ApplicationContextWrapper.getContext().ifPresent(context -> {
        if (context.containsBean("loggingSensitiveProperties")) {
          loggingSensitiveProperties = context.getBean(LoggingSensitiveProperties.class);
        }
      });
    }
    if (loggingSensitiveProperties == null) {
      return event.getFormattedMessage();
    } else {
      String msg = event.getFormattedMessage();
      try {
        return sensitiveRegexReplacement(msg);
      } catch (Exception e) {
        log.error("sensitive regex replacement fail:", e); // $COVERAGE-IGNORE$
        return msg; // $COVERAGE-IGNORE$
      }
    }
  }

  private String sensitiveRegexReplacement(String msg) {
    Iterator<Integer> it = loggingSensitiveProperties.getSensitive().keySet().iterator();
    while (it.hasNext()) {
      RegexReplacement regexReplacement = loggingSensitiveProperties.getSensitive().get(it.next());
      msg = msg.replaceAll(regexReplacement.getRegex(), regexReplacement.getReplacement());
    }
    return msg;
  }
}