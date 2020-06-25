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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.TimeoutException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

@SuppressWarnings("unchecked")
public class AsynchronousDeliveryStrategyTest {

  private final Producer<String, String> producer = mock(
      (Class<Producer<String, String>>) (Class) Producer.class);
  private final FailedDeliveryCallback<String> failedDeliveryCallback = mock(
      (Class<FailedDeliveryCallback<String>>) (Class) FailedDeliveryCallback.class);
  private final AsynchronousDeliveryStrategy unit = new AsynchronousDeliveryStrategy();

  private final TopicPartition topicAndPartition = new TopicPartition("topic", 0);
  private final RecordMetadata recordMetadata = new RecordMetadata(topicAndPartition, 0, 0, System
      .currentTimeMillis(), null, 32, 64);

  @Test
  public void testCallbackWillNotTriggerOnFailedDeliveryOnNoException() {
    final ProducerRecord<String, String> record = new ProducerRecord<String, String>("topic", 0,
        null, "msg");
    unit.send(producer, record, "msg", failedDeliveryCallback);

    final ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
    verify(producer).send(Mockito.refEq(record), callbackCaptor.capture());

    final Callback callback = callbackCaptor.getValue();
    callback.onCompletion(recordMetadata, null);

    verify(failedDeliveryCallback, never()).onFailedDelivery(anyString(), any(Throwable.class));
  }

  @Test
  public void testCallbackWillTriggerOnFailedDeliveryOnException() {
    final IOException exception = new IOException("KABOOM");
    final ProducerRecord<String, String> record = new ProducerRecord<String, String>("topic", 0,
        null, "msg");
    unit.send(producer, record, "msg", failedDeliveryCallback);

    final ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
    verify(producer).send(Mockito.refEq(record), callbackCaptor.capture());

    final Callback callback = callbackCaptor.getValue();
    callback.onCompletion(recordMetadata, exception);

    verify(failedDeliveryCallback).onFailedDelivery("msg", exception);
  }

  @Test
  public void testCallbackWillTriggerOnFailedDeliveryOnProducerSendTimeout() {
    final TimeoutException exception = new TimeoutException("miau");
    final ProducerRecord<String, String> record = new ProducerRecord<String, String>("topic", 0,
        null, "msg");

    when(producer.send(same(record), any(Callback.class))).thenThrow(exception);

    unit.send(producer, record, "msg", failedDeliveryCallback);

    verify(failedDeliveryCallback).onFailedDelivery(eq("msg"), same(exception));
  }

}
