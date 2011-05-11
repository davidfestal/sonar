package org.sonar.plugins.i18n;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.SonarPlugin;

/**
 * This class is the entry point for all extensions
 */
public class I18nPlugin extends SonarPlugin {

  // This is where you're going to declare all your Sonar extensions
  public List getExtensions() {
    return Arrays.asList(I18nManager.class);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
