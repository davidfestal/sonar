package org.sonar.plugins.i18n.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.sonar.api.i18n.LanguagePack;

public class FrenchLanguagePack extends LanguagePack {

  @Override
  public List<String> getPluginKeys() {
    return Arrays.asList("test");
  }

  @Override
  public List<Locale> getLocales() {
    return Arrays.asList(Locale.FRENCH);
  }
}