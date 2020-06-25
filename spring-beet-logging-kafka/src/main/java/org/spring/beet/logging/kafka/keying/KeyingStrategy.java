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

/**
 * A strategy that can create byte array key for a given {@link ILoggingEvent}.
 */
public interface KeyingStrategy<E> {

  /**
   * creates a byte array key for the given {@link ILoggingEvent}
   *
   * @param e the logging event
   * @return a key
   */
  byte[] createKey(E e);

}
