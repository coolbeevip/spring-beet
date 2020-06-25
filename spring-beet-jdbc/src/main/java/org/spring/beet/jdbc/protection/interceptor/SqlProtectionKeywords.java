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

/**
 * @author zhanglei
 */
public enum SqlProtectionKeywords {

  ALTER("alter"),
  CREATEINDEX("createindex"),
  CREATETABLE("createtable"),
  CREATEVIEW("createview"),
  DELETE("delete"),
  DROP("drop"),
  EXECUTE("execute"),
  INSERT("insert"),
  MERGE("merge"),
  REPLACE("replace"),
  SELECT("select"),
  TRUNCATE("truncate"),
  UPDATE("update"),
  UPSERT("upsert"),
  NONE("none");

  private String type;

  private SqlProtectionKeywords(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
