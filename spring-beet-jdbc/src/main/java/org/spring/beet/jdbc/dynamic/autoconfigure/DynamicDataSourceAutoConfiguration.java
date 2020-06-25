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
package org.spring.beet.jdbc.dynamic.autoconfigure;

import com.google.common.collect.ImmutableMap;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.spring.beet.jdbc.dynamic.DynamicDataSource;
import org.spring.beet.jdbc.dynamic.aspect.DynamicDataSourceAspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

/**
 * @author zhanglei
 */
@Configuration
@ConditionalOnClass(DynamicDataSourceAspect.class)
@AutoConfigureAfter(FlywayAutoConfiguration.class)
public class DynamicDataSourceAutoConfiguration implements EnvironmentAware {


  public static final String SPRING_DYNAMIC_DATASOURCE_PREFIX = "spring.dynamicdatasource";
  public static final String SPRING_FLYWAY_POOL_NAME = "spring.flyway.poolName";
  public static final String DEFAULT_DATASOURCE = "default";
  public static final String HIKARI_PREFIX = ".hikari";

  private Environment environment;

  @SuppressWarnings("unchecked")
  private WeakHashMap<String, DataSource> dataSourceMap = new WeakHashMap();

  @Override
  public void setEnvironment(final Environment environment) {
    this.environment = environment;
  }

  @Bean
  @SuppressWarnings("unchecked")
  public DynamicDataSource dynamicDataSource(@Autowired DataSource defaultDataSource) {
    DynamicDataSource dynamicDataSource = new DynamicDataSource();
    dynamicDataSource.setDefaultTargetDataSource(defaultDataSource);
    Map<String, DataSource> ds = initDynamicDataSource(SPRING_DYNAMIC_DATASOURCE_PREFIX);
    Map dataSources = ImmutableMap.<String, DataSource>builder()
        .put(DEFAULT_DATASOURCE, defaultDataSource)
        .putAll(ds).build();
    dynamicDataSource.setTargetDataSources(dataSources);
    dataSourceMap.putAll(dataSources);
    return dynamicDataSource;
  }

  private Map<String, DataSource> initDynamicDataSource(String prefix) {
    Map<String, DataSource> ds = new HashMap<>();
    Properties dynamicDataSourceProperties = Binder.get(environment)
        .bind(prefix, Properties.class)
        .orElse(null);
    if (dynamicDataSourceProperties != null) {
      Map<Object, List<Object>> dynamicDataSourceMap = dynamicDataSourceProperties.keySet().stream()
          .collect(
              Collectors.groupingBy(a -> a.toString().substring(0, a.toString().indexOf("."))));
      dynamicDataSourceMap.entrySet().forEach(entry -> {
        String dataSourceName = entry.getKey().toString();
        DataSourceProperties properties = Binder.get(environment)
            .bind(prefix + "." + dataSourceName, DataSourceProperties.class).orElseGet(null);
        HikariConfig hikariConfig = Binder.get(environment)
            .bind(prefix + "." + dataSourceName + HIKARI_PREFIX, HikariConfig.class)
            .orElseGet(null);
        hikariConfig.setJdbcUrl(properties.getUrl());
        hikariConfig.setUsername(properties.getUsername());
        hikariConfig.setPassword(properties.getPassword());
        ds.put(dataSourceName, new HikariDataSource(hikariConfig));
      });
    }
    return ds;
  }

  @Bean
  @Primary
  public SqlSessionFactoryBean sqlSessionFactoryBean(@Autowired DynamicDataSource dynamicDataSource,
      @Autowired DatabaseIdProvider databaseIdProvider)
      throws Exception {
    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(dynamicDataSource);
    sqlSessionFactoryBean.setDatabaseIdProvider(databaseIdProvider);
    sqlSessionFactoryBean.afterPropertiesSet();
    return sqlSessionFactoryBean;
  }

  @FlywayDataSource
  @Bean
  public DataSource someDatasource(
      @Value("${" + SPRING_FLYWAY_POOL_NAME + ":" + DEFAULT_DATASOURCE + "}") String poolName) {
    return dataSourceMap.get(poolName);
  }

  @Bean
  @ConditionalOnMissingBean
  public DynamicDataSourceAspect dynamicDataSourceAspect() {
    return new DynamicDataSourceAspect();
  }
}