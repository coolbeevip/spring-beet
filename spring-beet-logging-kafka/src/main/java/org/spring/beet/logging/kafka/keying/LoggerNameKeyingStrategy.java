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
package org.spring.beet.logging.kafka.keying;

import ch.qos.logback.classic.spi.ILoggingEvent;
import java.nio.ByteBuffer;

/**
 * This strategy uses the logger name as partitioning key. This ensures that all messages logged by
 * the same logger will remain in the correct order for any consumer. But this strategy can lead to
 * uneven log distribution for a small number of distinct loggers (compared to the number of
 * partitions).
 */
public class LoggerNameKeyingStrategy implements KeyingStrategy<ILoggingEvent> {

  @Override
  public byte[] createKey(ILoggingEvent e) {
    final String loggerName;
    if (e.getLoggerName() == null) {
      loggerName = "";
    } else {
      loggerName = e.getLoggerName();
    }
    return ByteBuffer.allocate(4).putInt(loggerName.hashCode()).array();
  }

}
