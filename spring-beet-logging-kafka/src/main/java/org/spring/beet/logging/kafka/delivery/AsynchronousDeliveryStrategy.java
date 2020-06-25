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

import org.apache.kafka.clients.producer.BufferExhaustedException;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.TimeoutException;

public class AsynchronousDeliveryStrategy implements DeliveryStrategy {

  @Override
  public <K, V, E> boolean send(Producer<K, V> producer, ProducerRecord<K, V> record, final E event,
      final FailedDeliveryCallback<E> failedDeliveryCallback) {
    try {
      producer.send(record, new Callback() {
        @Override
        public void onCompletion(RecordMetadata metadata, Exception exception) {
          if (exception != null) {
            failedDeliveryCallback.onFailedDelivery(event, exception);
          }
        }
      });
      return true;
    } catch (BufferExhaustedException | TimeoutException e) {
      failedDeliveryCallback.onFailedDelivery(event, e);
      return false;
    }
  }

}
