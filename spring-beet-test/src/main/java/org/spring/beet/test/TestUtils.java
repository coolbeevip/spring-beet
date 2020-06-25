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
package org.spring.beet.test;

import java.io.File;
import java.net.ServerSocket;
import java.util.Random;
import lombok.SneakyThrows;
import org.springframework.util.FileSystemUtils;

public class TestUtils {

  private static final Random RANDOM = new Random();

  public static File constructTempDir(String dirPrefix) {
    File file = new File(System.getProperty("java.io.tmpdir"),
        dirPrefix + RANDOM.nextInt(10000000));
    if (!file.mkdirs()) {
      throw new RuntimeException("could not create temp directory: " + file.getAbsolutePath());
    }
    file.deleteOnExit();
    return file;
  }

  @SneakyThrows
  public static int getAvailablePort() {
    ServerSocket socket = new ServerSocket(0);
    try {
      return socket.getLocalPort();
    } finally {
      socket.close();
    }
  }

  public static boolean deleteFile(File path) {
    return FileSystemUtils.deleteRecursively(path);
  }
}