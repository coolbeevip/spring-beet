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
package org.spring.beet.jdbc.protection.autoconfigure;

import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.spring.beet.jdbc.protection.interceptor.SqlProtectionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author zhanglei
 */
@Slf4j
@Configuration
@EnableAspectJAutoProxy
@AutoConfigureAfter(PageHelperAutoConfiguration.class) //must
@ConditionalOnProperty(value = "spring.beet.datasource.sql.protection.enabled")
public class SqlProtectionAutoConfiguration {

  @Value("${spring.beet.datasource.sql.protection.keywords}")
  public String[] keywords;
  @Autowired
  private List<SqlSessionFactory> sqlSessionFactoryList;

  @PostConstruct
  public void addDangerSqlInterceptor() {
    SqlProtectionInterceptor interceptor = new SqlProtectionInterceptor(keywords);
    Iterator it = this.sqlSessionFactoryList.iterator();
    while (it.hasNext()) {
      SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) it.next();
      sqlSessionFactory.getConfiguration().addInterceptor(interceptor);
    }
  }
}
