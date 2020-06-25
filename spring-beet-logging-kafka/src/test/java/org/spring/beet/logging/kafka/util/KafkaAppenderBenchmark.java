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
package org.spring.beet.logging.kafka.util;

import ch.qos.logback.classic.Logger;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.annotation.AxisRange;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkHistoryChart;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkMethodChart;
import com.carrotsearch.junitbenchmarks.annotation.LabelType;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.slf4j.LoggerFactory;

@AxisRange(min = 0, max = 5)
@BenchmarkMethodChart(filePrefix = "benchmark-lists")
@BenchmarkHistoryChart(labelWith = LabelType.CUSTOM_KEY, maxRuns = 20)
public class KafkaAppenderBenchmark {

  @Rule
  public TestRule benchmarkRun = new BenchmarkRule();

  private Logger logger;

  @Before
  public void before() {
    LoggerFactory.getLogger("triggerLogInitialization");
    logger = (Logger) LoggerFactory.getLogger("IT");
  }

  @After
  public void after() {

  }

  @Ignore
  @BenchmarkOptions(benchmarkRounds = 10, warmupRounds = 2, concurrency = 8)
  @Test
  public void benchmark() throws InterruptedException {
    for (int i = 0; i < 100000; ++i) {
      logger.info("A VERY IMPORTANT LOG MESSAGE {}", i);
    }
  }

}
