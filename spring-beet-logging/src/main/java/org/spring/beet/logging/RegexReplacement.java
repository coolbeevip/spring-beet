package org.spring.beet.logging;

import lombok.Builder;

@Builder
public class RegexReplacement {

  public String regex;
  private String replacement;

  public String getRegex() {
    return regex;
  }

  public String getReplacement() {
    return replacement;
  }
}