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
package org.spring.beet.cloud.eureka;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.spring.beet.common.constant.SpringBeetConstant;
import org.spring.beet.common.springboot.ApplicationContextWrapper;

@Slf4j
public class NamespaceRoundRibbonRule extends ZoneAvoidanceRule {

  private AtomicInteger nextServerCyclicCounter;

  public NamespaceRoundRibbonRule() {
    this.nextServerCyclicCounter = new AtomicInteger(0);
  }

  public NamespaceRoundRibbonRule(ILoadBalancer lb) {
    this();
    this.setLoadBalancer(lb);
  }

  public Server choose(ILoadBalancer lb, Object key) {
    if (lb == null) {
      log.warn("no load balancer");
      return null;
    } else {
      // 获取与本服务命名空间相同的服务
      Optional<String> selfNamespace = ApplicationContextWrapper
          .getProperty(SpringBeetConstant.EUREKA_METADATA_NAMESPACE_KEY);
      List<Server> allServers = this.getPredicate().getEligibleServers(this.getLoadBalancer().getAllServers(), key);
      List<Server> matchNamespaceServers = allServers.stream().filter(server -> {
        if (server instanceof DiscoveryEnabledServer) {
          Map<String, String> metaMap = ((DiscoveryEnabledServer) server).getInstanceInfo()
              .getMetadata();
          if (metaMap.get(SpringBeetConstant.NAMESPACE_KEY).equals(selfNamespace.get())) {
            return true;
          }
        }
        return false;
      }).collect(Collectors.toList());
      Server server = null;
      int count = 0;

      while (true) {
        if (server == null && count++ < 10) {
          List<Server> reachableServers = lb.getReachableServers();
          // TODO 后续分析 reachableServers.size() 为什么是 0
          int upCount = reachableServers.size();
          int serverCount = matchNamespaceServers.size();
          if (upCount != 0 || serverCount != 0) {
            int nextServerIndex = this.incrementAndGetModulo(serverCount);
            server = matchNamespaceServers.get(nextServerIndex);
            if (server == null) {
              Thread.yield();
            } else {
              // TODO server.isAlive() && 后续分析为什么是false
              if (server.isReadyToServe()) {
                return server;
              }

              server = null;
            }
            continue;
          }

          log.warn("No up servers available from load balancer: " + lb);
          return null;
        }

        if (count >= 10) {
          log.warn("No available alive servers after 10 tries from load balancer: " + lb);
        }
      }
    }
  }

  public Server choose(Object key) {
    return this.choose(this.getLoadBalancer(), key);
  }

  public void initWithNiwsConfig(IClientConfig clientConfig) {
  }

  private int incrementAndGetModulo(int modulo) {
    int current;
    int next;
    do {
      current = this.nextServerCyclicCounter.get();
      next = (current + 1) % modulo;
    } while (!this.nextServerCyclicCounter.compareAndSet(current, next));

    return next;
  }
}