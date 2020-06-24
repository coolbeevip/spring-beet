package org.spring.beet.logging;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoggingSensitiveProperties {

  public Map<Integer, RegexReplacement> sensitive = new HashMap<>();
}