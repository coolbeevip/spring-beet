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

import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import java.nio.ByteBuffer;

/**
 * This strategy uses the HOSTNAME as kafka message key. This is useful because it ensures that all
 * log messages issued by this host will remain in the correct order for any consumer. But this
 * strategy can lead to uneven log distribution for a small number of hosts (compared to the number
 * of partitions).
 */
public class HostNameKeyingStrategy extends ContextAwareBase implements KeyingStrategy<Object>,
    LifeCycle {

  private byte[] hostnameHash = null;
  private boolean errorWasShown = false;

  @Override
  public void setContext(Context context) {
    super.setContext(context);
    final String hostname = context.getProperty(CoreConstants.HOSTNAME_KEY);
    if (hostname == null) {
      if (!errorWasShown) {
        addError(
            "Hostname could not be found in context. HostNamePartitioningStrategy will not work.");
        errorWasShown = true;
      }
    } else {
      hostnameHash = ByteBuffer.allocate(4).putInt(hostname.hashCode()).array();
    }
  }

  @Override
  public byte[] createKey(Object e) {
    return hostnameHash.clone();
  }

  @Override
  public void start() {
  }

  @Override
  public void stop() {
    errorWasShown = false;
  }

  @Override
  public boolean isStarted() {
    return true;
  }
}
