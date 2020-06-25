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
import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.nio.ByteBuffer;

/**
 * This strategy uses logbacks CONTEXT_NAME as kafka message key. This is ensures that all log
 * messages logged by the same logging context will remain in the correct order for any consumer.
 * But this strategy can lead to uneven log distribution for a small number of hosts (compared to
 * the number of partitions).
 */
public class ContextNameKeyingStrategy extends ContextAwareBase implements
    KeyingStrategy<ILoggingEvent> {

  private byte[] contextNameHash = null;

  @Override
  public void setContext(Context context) {
    super.setContext(context);
    final String hostname = context.getProperty(CoreConstants.CONTEXT_NAME_KEY);
    if (hostname == null) {
      addError(
          "Hostname could not be found in context. HostNamePartitioningStrategy will not work.");
    } else {
      contextNameHash = ByteBuffer.allocate(4).putInt(hostname.hashCode()).array();
    }
  }

  @Override
  public byte[] createKey(ILoggingEvent e) {
    return contextNameHash.clone();
  }
}
