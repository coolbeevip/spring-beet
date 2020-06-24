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
package org.spring.beet.web.undertow;

import javax.net.ssl.SSLContext;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class UndertowAutoConfiguration {

  @Bean
  public UndertowWebServerFactoryCustomizer customizationBean(
      @Value("${server.undertow.buffer-pool:1024}") int bufferPool,
      @Value("${server.host:0.0.0.0}") String serverHost,
      @Value("${server.secondary.port:0}") int serverSecondaryPort) {
    return new UndertowWebServerFactoryCustomizer(bufferPool, serverHost, serverSecondaryPort);
  }

  //@Primary
  @ConditionalOnProperty(name = "server.ssl.enabled", havingValue = "true")
  @Bean(name="SpringBeetSSLRestTemplate")
  RestTemplate springBeetSSLRestTemplate(
      @Value("${trust.store}") Resource trustStore,
      @Value("${trust.store.password}") String trustStorePassword)
      throws Exception {
    SSLContext sslContext =
        new SSLContextBuilder()
            .loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray())
            .build();
    SSLConnectionSocketFactory socketFactory =
        new SSLConnectionSocketFactory(
            sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
    HttpComponentsClientHttpRequestFactory factory =
        new HttpComponentsClientHttpRequestFactory(httpClient);
    return new RestTemplate(factory);
  }
}
