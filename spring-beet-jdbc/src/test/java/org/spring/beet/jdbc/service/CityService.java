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
package org.spring.beet.jdbc.service;

import java.util.List;
import org.spring.beet.jdbc.domain.City;
import org.spring.beet.jdbc.mapper.MasterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zhanglei
 */
@Service
public class CityService {

  @Autowired
  MasterMapper masterMapper;

  @Transactional
  public void addCityThrowException() {
    masterMapper.insert(City.builder().id("755").name("深圳").country("中国").state("广东").build());
    masterMapper.insert(City.builder().id("755").name("深圳").country("中国").state("广东").build());
  }

  @Transactional
  public void addCityNestedThrowException() {
    masterMapper.insert(City.builder().id("20").name("广州").country("中国").state("广东").build());
    addCityThrowException();
  }

  public List<City> getAllCity() {
    return masterMapper.getAll();
  }
}