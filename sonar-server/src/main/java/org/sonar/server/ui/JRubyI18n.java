/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2011 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.server.ui;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.ServerComponent;
import org.sonar.api.i18n.I18n;

import java.util.Locale;
import java.util.Map;

/**
 * Bridge between JRuby webapp and Java I18n component
 */
public final class JRubyI18n implements ServerComponent {

  private I18n i18n;
  private Map<String,Locale> localesByRubyKey = Maps.newHashMap();

  public JRubyI18n(I18n i18n) {
    this.i18n = i18n;
  }

  Locale getLocale(String rubyKey) {
    Locale locale = localesByRubyKey.get(rubyKey);
    if (locale == null) {
      locale = toLocale(rubyKey);
      localesByRubyKey.put(rubyKey, locale);
    }
    return locale;
  }

  Map<String, Locale> getLocalesByRubyKey() {
    return localesByRubyKey;
  }

  static Locale toLocale(String rubyKey) {
    Locale locale;
    String[] fields = StringUtils.split(rubyKey, "-");
    if (fields.length==1) {
      locale = new Locale(fields[0]);
    } else {
      locale = new Locale(fields[0], fields[1]);
    }
    return locale;
  }

  public String message(String rubyLocale, String key, String defaultValue, Object... parameters) {
    return i18n.message(getLocale(rubyLocale), key, defaultValue, parameters);
  }
}
