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
package org.spring.beet.logging.kafka.delivery;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * Interface for DeliveryStrategies.
 */
public interface DeliveryStrategy {

  /**
   * Sends a message to a kafka producer and somehow deals with failures.
   *
   * @param producer the backing kafka producer
   * @param record the prepared kafka message (ready to ship)
   * @param event the originating logging event
   * @param failedDeliveryCallback a callback that handles messages that could not be delivered with best-effort.
   * @param <K> the key type of a persisted log message.
   * @param <V> the value type of a persisted log message.
   * @param <E> the type of the logging event.
   * @return {@code true} if the message could be sent successfully, {@code false} otherwise.
   */
  <K, V, E> boolean send(Producer<K, V> producer, ProducerRecord<K, V> record, E event,
      FailedDeliveryCallback<E> failedDeliveryCallback);

}
