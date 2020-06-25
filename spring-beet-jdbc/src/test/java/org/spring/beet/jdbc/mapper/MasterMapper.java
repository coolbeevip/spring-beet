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
package org.spring.beet.jdbc.mapper;

import java.sql.Date;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.spring.beet.jdbc.domain.City;

/**
 * @author zhanglei
 */
@Mapper
public interface MasterMapper {

  @Select("select id, name, state, country from city")
  List<City> getAll();

  @Select("select id, name, state, country from city where id=#{id}")
  City getOne(@Param("id") String id);

  @Insert("insert into city(id, name, state, country) values (#{id}, #{name}, #{state}, #{country})")
  void insert(City city);

  @Update("update city set name=#{name},state=#{state},country=#{state} where id=#{id}")
  void update(City city);

  @Delete("delete from city where id=#{id}")
  void delete(String id);

  @Delete("delete from city")
  void deleteAll();

  @Update("drop table city")
  void dropTable();

  @Select({"<script>", " select"
      + " <if test=\"_databaseId == 'sqlite'\">strftime('%Y-%m-%d %H:%M:%f','now')</if>"
      + " <if test=\"_databaseId == 'h2'\">current_timestamp()</if>"
      + "</script>"})
  Date getNow();
}