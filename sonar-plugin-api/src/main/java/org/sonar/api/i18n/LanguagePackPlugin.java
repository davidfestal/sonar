package org.sonar.api.i18n;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.sonar.api.SonarPlugin;


abstract public class LanguagePackPlugin extends SonarPlugin {
  
  @Override
  public String toString() {
    return new StringBuilder("Language Pack (")
    .append(getPluginKeys().toString())
    .append(getLocales().toString())
    .append(')').toString();
  }

  public List getExtensions() {
    return Collections.emptyList();
  }
  
  /**
   * @return the pluginKeys
   */
  public abstract List<String> getPluginKeys();

  /**
   * @return the locales
   */
  public abstract List<Locale> getLocales();
}
