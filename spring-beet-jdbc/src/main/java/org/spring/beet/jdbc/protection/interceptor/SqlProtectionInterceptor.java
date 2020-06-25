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
package org.spring.beet.jdbc.protection.interceptor;

import java.io.StringReader;
import java.sql.Connection;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.upsert.Upsert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.spring.beet.common.exception.DangerException;

/**
 * Detect danger keywords in SQL.
 *
 * @author zhanglei
 */
@Slf4j
@Intercepts(value = @Signature(method = "prepare", type = StatementHandler.class, args = {
    Connection.class, Integer.class}))
public class SqlProtectionInterceptor implements Interceptor {

  private String[] keywords;

  public SqlProtectionInterceptor(String[] keywords) {
    this.keywords = keywords.clone();
  }

  public static SqlProtectionKeywords getSqlType(String sql)
      throws JSQLParserException {
    Statement sqlStmt = CCJSqlParserUtil.parse(new StringReader(sql));
    if (sqlStmt instanceof Alter) {
      return SqlProtectionKeywords.ALTER;
    } else if (sqlStmt instanceof CreateIndex) {
      return SqlProtectionKeywords.CREATEINDEX;
    } else if (sqlStmt instanceof CreateTable) {
      return SqlProtectionKeywords.CREATETABLE;
    } else if (sqlStmt instanceof CreateView) {
      return SqlProtectionKeywords.CREATEVIEW;
    } else if (sqlStmt instanceof Delete) {
      return SqlProtectionKeywords.DELETE;
    } else if (sqlStmt instanceof Drop) {
      return SqlProtectionKeywords.DROP;
    } else if (sqlStmt instanceof Execute) {
      return SqlProtectionKeywords.EXECUTE;
    } else if (sqlStmt instanceof Insert) {
      return SqlProtectionKeywords.INSERT;
    } else if (sqlStmt instanceof Merge) {
      return SqlProtectionKeywords.MERGE;
    } else if (sqlStmt instanceof Replace) {
      return SqlProtectionKeywords.REPLACE;
    } else if (sqlStmt instanceof Select) {
      return SqlProtectionKeywords.SELECT;
    } else if (sqlStmt instanceof Truncate) {
      return SqlProtectionKeywords.TRUNCATE;
    } else if (sqlStmt instanceof Update) {
      return SqlProtectionKeywords.UPDATE;
    } else if (sqlStmt instanceof Upsert) {
      return SqlProtectionKeywords.UPSERT;
    } else {
      return SqlProtectionKeywords.NONE;
    }
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    StatementHandler handler = (StatementHandler) invocation.getTarget();
    BoundSql boundSql = handler.getBoundSql();
    String sql = boundSql.getSql();
    if (keywords != null) {
      String sqltype = getSqlType(sql).getType();
      for (String dangerType : keywords) {
        if (dangerType.toLowerCase().equals(sqltype)) {
          MetaObject statementHandler = SystemMetaObject.forObject(handler);
          MappedStatement mappedStatement = (MappedStatement) statementHandler
              .getValue("delegate.mappedStatement");
          log.error("Danger SQL keyword [{}] detected from [{}={}] "
                  + "You can set spring.beet.sql.danger.enabled=false to disable SQL threat protection "
                  + "or remove keyword [{}] from spring.beet.sql.danger.type", dangerType,
              mappedStatement.getId(), sql, dangerType);
          throw new DangerException();
        }
      }
    }
    return invocation.proceed();
  }

  @Override
  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  @Override
  public void setProperties(Properties properties) {

  }

}
