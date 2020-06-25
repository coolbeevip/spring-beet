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
package org.spring.beet.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

public class DataSourceAutoConfiguration implements EnvironmentAware {

  public static final String SPRING_DATASOURCE_PREFIX = "spring.datasource";
  public static final String HIKARI_PREFIX = ".hikari";
  private Environment environment;

  @Override
  public void setEnvironment(final Environment environment) {
    this.environment = environment;
  }

  @Bean(name = "defaultDataSource")
  public DataSource defaultDataSource() {
    return bindDataSource(SPRING_DATASOURCE_PREFIX);
  }

  private DataSource bindDataSource(String prefix) {
    DataSourceProperties properties = Binder.get(environment)
        .bind(prefix, DataSourceProperties.class).orElse(null);
    HikariConfig hikariConfig = Binder.get(environment)
        .bind(prefix + HIKARI_PREFIX, HikariConfig.class).orElseGet(null);
    hikariConfig.setJdbcUrl(properties.getUrl());
    hikariConfig.setUsername(properties.getUsername());
    hikariConfig.setPassword(properties.getPassword());
    return new HikariDataSource(hikariConfig);
  }

  @Bean
  public DatabaseIdProvider databaseIdProvider() {
    Properties p = new Properties();
    p.setProperty("Oracle", "oracle");
    p.setProperty("MySQL", "mysql");
    p.setProperty("H2", "h2");
    p.setProperty("SQLite", "sqlite");
    p.setProperty("SQL Server", "sqlserver");
    p.setProperty("DB2", "db2");
    p.setProperty("PostgreSQL", "postgresql");
    DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
    databaseIdProvider.setProperties(p);
    return databaseIdProvider;
  }
}