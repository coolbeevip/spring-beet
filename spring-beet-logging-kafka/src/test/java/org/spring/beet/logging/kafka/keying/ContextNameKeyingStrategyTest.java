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

import static org.hamcrest.MatcherAssert.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import java.nio.ByteBuffer;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
public class ContextNameKeyingStrategyTest {

  private static final String LOGGER_CONTEXT_NAME = "loggerContextName";
  private final ContextNameKeyingStrategy unit = new ContextNameKeyingStrategy();
  private final LoggerContext ctx = new LoggerContext();

  @Test
  public void shouldPartitionByEventThreadName() {
    ctx.setName(LOGGER_CONTEXT_NAME);
    unit.setContext(ctx);
    final ILoggingEvent evt = new LoggingEvent("fqcn", ctx.getLogger("logger"), Level.ALL, "msg",
        null, new Object[0]);
    assertThat(unit.createKey(evt),
        Matchers.equalTo(ByteBuffer.allocate(4).putInt(LOGGER_CONTEXT_NAME.hashCode()).array()));
  }


}