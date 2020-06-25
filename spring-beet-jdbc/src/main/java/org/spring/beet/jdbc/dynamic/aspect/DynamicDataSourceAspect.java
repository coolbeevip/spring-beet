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
package org.spring.beet.jdbc.dynamic.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.spring.beet.jdbc.dynamic.DataSourceContextHolder;
import org.spring.beet.jdbc.dynamic.annotation.DynamicDataSource;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author zhanglei
 */
@Aspect
public class DynamicDataSourceAspect {

  @Around(value = "execution(* *..mapper*..*(..))")
  public Object getTargetDataSource(ProceedingJoinPoint point) throws Throwable {
    Object target = point.getTarget();
    MethodSignature signature = (MethodSignature) point.getSignature();

    DynamicDataSource dynamicDataSource = AnnotationUtils
        .findAnnotation(signature.getMethod(), DynamicDataSource.class);

    if (dynamicDataSource == null) {
      dynamicDataSource = AnnotationUtils
          .findAnnotation(target.getClass(), DynamicDataSource.class);
    }

    if (dynamicDataSource == null) {
      return point.proceed();
    }

    Object result;
    try {
      DataSourceContextHolder.set(dynamicDataSource.value());
      result = point.proceed();
    } finally {
      DataSourceContextHolder.remove();
    }
    return result;
  }

}