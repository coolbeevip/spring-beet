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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.spring.beet.test.embedded.kafka.EmbeddedKafkaCluster;
import org.spring.beet.test.embedded.zookeeper.EmbeddedZookeeper;

public class EmbeddedKafka {

  private final EmbeddedZookeeper zookeeper;
  private final EmbeddedKafkaCluster kafkaCluster;

  EmbeddedKafka(EmbeddedZookeeper zookeeper, EmbeddedKafkaCluster kafkaCluster) {
    this.zookeeper = zookeeper;
    this.kafkaCluster = kafkaCluster;
  }

  public static EmbeddedKafka createTestKafka(int partitionCount,
      int replicationFactor, List<Integer> brokerPorts) throws IOException, InterruptedException {
    final Map<String, String> properties = new HashMap<>();
    properties.put("num.partitions", Integer.toString(partitionCount));
    properties.put("default.replication.factor", Integer.toString(replicationFactor));

    return createTestKafka(brokerPorts, properties);
  }

  public static EmbeddedKafka createTestKafka(List<Integer> brokerPorts)
      throws IOException, InterruptedException {
    return createTestKafka(brokerPorts, Collections.<String, String>emptyMap());
  }

  public static EmbeddedKafka createTestKafka(List<Integer> brokerPorts,
      Map<String, String> properties)
      throws IOException, InterruptedException {
    if (properties == null) {
      properties = Collections.emptyMap();
    }
    final EmbeddedZookeeper zk = new EmbeddedZookeeper(-1, 100);
    zk.startup();

    final EmbeddedKafkaCluster kafka = new EmbeddedKafkaCluster(zk.getConnection(), properties,
        brokerPorts);
    kafka.startup();
    return new EmbeddedKafka(zk, kafka);
  }

  public KafkaConsumer<byte[], byte[]> createClient() {
    return createClient(new HashMap<String, Object>());
  }

  public KafkaConsumer<byte[], byte[]> createClient(Map<String, Object> consumerProperties) {
    consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getBrokerList());
    //consumerProperties.put("group.id", "simple-consumer-" + new Random().nextInt());
    consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    consumerProperties.put("auto.offset.reset", "earliest");
    consumerProperties.put("key.deserializer", ByteArrayDeserializer.class.getName());
    consumerProperties.put("value.deserializer", ByteArrayDeserializer.class.getName());
    return new KafkaConsumer<>(consumerProperties);
  }

  public String getZookeeperConnection() {
    return zookeeper.getConnection();
  }

  public String getBrokerList() {
    return kafkaCluster.getBrokerList();
  }

  public void shutdown() {
    try {
      kafkaCluster.shutdown();
    } finally {
      zookeeper.shutdown();
    }
  }

  public void awaitShutdown() {
    kafkaCluster.awaitShutdown();
  }


}
