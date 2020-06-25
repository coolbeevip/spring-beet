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
package org.spring.beet.test.embedded;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Slf4j
public class EmbeddedKafkaTest {

  private static EmbeddedKafka kafka;

  private static KafkaTemplate<String, String> kafkaTemplate;

  @Test
  public void producerAndConsumerTest() {
    final String topicName = "spring.beet.topic-test";
    final String message = "Hello";
    kafkaTemplate.send(topicName, message);
    kafkaTemplate.flush();
    final KafkaConsumer<byte[], byte[]> client = kafka.createClient();
    client.assign(Collections.singletonList(new TopicPartition(topicName, 0)));
    client.seekToBeginning(Collections.singletonList(new TopicPartition(topicName, 0)));
    ConsumerRecords<byte[], byte[]> consumerRecords = client.poll(Duration.ofMillis(1000));
    while (!consumerRecords.isEmpty()) {
      for (ConsumerRecord<byte[], byte[]> consumerRecord : consumerRecords) {
        final String messageFromKafka = new String(consumerRecord.value(),
            Charset.forName("UTF-8"));
        assertThat(messageFromKafka, Matchers.equalTo(message));
      }
      consumerRecords = client.poll(Duration.ofMillis(1000));
    }
  }

  @BeforeAll
  public static void setUp() throws IOException, InterruptedException {
    kafka = EmbeddedKafka.createTestKafka(1, 1, Arrays.asList(-1));
    kafkaTemplate = new KafkaTemplate<>(producerFactory());
  }

  @AfterAll
  public static void tearDown() {
    kafkaTemplate.flush();
    kafkaTemplate.destroy();
    kafka.shutdown();
    kafka.awaitShutdown();
  }

  private static ProducerFactory<String, String> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        kafka.getBrokerList());
    configProps.put(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class);
    configProps.put(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class);
    return new DefaultKafkaProducerFactory<>(configProps);
  }
}
