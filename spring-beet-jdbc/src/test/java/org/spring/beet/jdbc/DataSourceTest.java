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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.MyBatisSystemException;
import org.spring.beet.jdbc.domain.City;
import org.spring.beet.jdbc.mapper.MasterMapper;
import org.spring.beet.jdbc.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author zhanglei
 */
@Slf4j
@SpringBootTest(properties = {"spring.beet.datasource.sql.protection.enabled=true"})
public class DataSourceTest {

  @Autowired
  private MasterMapper masterMapper;

  @Autowired
  private CityService cityService;

  @BeforeEach
  public void setup() {
    masterMapper.insert(City.builder().id("10").name("北京").country("中国").state("北京").build());
    masterMapper.insert(City.builder().id("21").name("上海").country("中国").state("上海").build());
    masterMapper.insert(City.builder().id("335").name("秦皇岛").country("中国").state("河北").build());
  }

  @AfterEach
  public void unsetup() {
    masterMapper.deleteAll();
  }

  @Test
  public void testInsert() {
    assertEquals(3, masterMapper.getAll().size());
  }

  @Test
  public void testQuery() {
    City city = masterMapper.getOne("21");
    assertTrue(("上海".equals(city.getName())));
  }

  @Test
  public void testUpdate() {
    City city = masterMapper.getOne("10");
    city.setName("beijing");
    masterMapper.update(city);
    assertTrue(("beijing".equals(masterMapper.getOne("10").getName())));
  }

  @Test
  public void testDelete() {
    masterMapper.delete("335");
    assertEquals(2, masterMapper.getAll().size());
  }

  @Test
  public void testPage() {
    PageHelper.startPage(1, 2);
    List<City> cities = masterMapper.getAll();
    assertEquals(2, cities.size());
    assertEquals(3, ((Page) cities).getTotal());
  }

  @Test
  public void testTransactional() {
    try {
      cityService.addCityThrowException();
    } catch (Exception e) {
      log.error("", e);
    }
    assertEquals(3, cityService.getAllCity().size());
  }

  @Test
  public void testNestedTransactional() {
    try {
      cityService.addCityNestedThrowException();
    } catch (Exception e) {
      log.error("", e);
    }
    assertEquals(3, cityService.getAllCity().size());
  }

  @Test
  public void testWithSqlProtection() {
    assertThatThrownBy(() -> masterMapper.dropTable()).isInstanceOf(MyBatisSystemException.class);
  }

  @Test
  public void testNow() {
    masterMapper.getNow();
  }
}