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
package org.spring.beet.test.embedded.zookeeper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import lombok.SneakyThrows;
import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.spring.beet.test.TestUtils;

public class EmbeddedZookeeper {

  private int port = -1;
  private int tickTime = 100;

  private ServerCnxnFactory factory;
  private File snapshotDir;
  private File logDir;

  public EmbeddedZookeeper(int port, int tickTime) {
    this.port = resolvePort(port);
    this.tickTime = tickTime;
  }

  private int resolvePort(int port) {
    if (port == -1) {
      return TestUtils.getAvailablePort();
    }
    return port;
  }

  public void startup() throws IOException, InterruptedException {
    if (this.port == -1) {
      this.port = TestUtils.getAvailablePort();
    }
    this.factory = NIOServerCnxnFactory
        .createFactory(new InetSocketAddress("localhost", port), 1024);
    this.snapshotDir = TestUtils.constructTempDir("embeeded-zk/snapshot");
    this.logDir = TestUtils.constructTempDir("embeeded-zk/log");
    final ZooKeeperServer zooKeeperServer = new ZooKeeperServer(snapshotDir, logDir, tickTime);
    factory.startup(zooKeeperServer);
    assertEquals("standalone", zooKeeperServer.getState());
    assertEquals(this.port, zooKeeperServer.getClientPort());

  }

  @SneakyThrows
  public void shutdown() {
    factory.shutdown();
    factory.join();
    TestUtils.deleteFile(snapshotDir);
    TestUtils.deleteFile(logDir);
  }

  public String getConnection() {
    return "localhost:" + port;
  }

  @Override
  public String toString() {
    return "EmbeddedZookeeper{" + "connection=" + getConnection() + '}';
  }
}
