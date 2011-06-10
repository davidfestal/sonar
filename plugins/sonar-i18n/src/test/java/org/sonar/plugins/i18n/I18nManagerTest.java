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
package org.sonar.plugins.i18n;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.i18n.LanguagePack;
import org.sonar.core.plugin.AbstractPluginRepository;
import org.sonar.core.plugin.AbstractPluginRepositoryTest.A;
import org.sonar.core.plugin.AbstractPluginRepositoryTest.B;
import org.sonar.core.plugin.AbstractPluginRepositoryTest.BProvider;
import org.sonar.core.plugin.AbstractPluginRepositoryTest.C;
import org.sonar.core.plugin.AbstractPluginRepositoryTest.D;
import org.sonar.plugins.i18n.utils.FakePlugin;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparisons.greaterThanOrEqualTo;

public class I18nManagerTest {

  public static String TEST_PLUGIN_CLASS_NAME = "org.sonar.plugins.i18n.utils.StandardPlugin";
  public static String FRENCH_PACK_CLASS_NAME = "org.sonar.plugins.i18n.utils.FrenchLanguagePack";
  public static String QUEBEC_PACK_CLASS_NAME = "org.sonar.plugins.i18n.utils.QuebecLanguagePack";

  private I18nManager manager;

  @Before
  public void createManager() throws MalformedURLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    AbstractPluginRepository pluginRepository = mock(AbstractPluginRepository.class);
    TestClassLoader testPluginClassLoader = new TestClassLoader(getClass().getClassLoader().getResource("StandardPlugin.jar"));
    Plugin testPlugin = (Plugin) testPluginClassLoader.loadClass(TEST_PLUGIN_CLASS_NAME).newInstance();
    Plugin fakePlugin1 = new FakePlugin();
    Plugin fakePlugin2 = new FakePlugin();
    when(pluginRepository.getPlugins()).thenReturn(Arrays.asList(fakePlugin1, testPlugin, fakePlugin2));
    when(pluginRepository.getPluginKey(testPlugin)).thenReturn("test");
    when(pluginRepository.getPluginKey(fakePlugin1)).thenReturn("fake1");
    when(pluginRepository.getPluginKey(fakePlugin1)).thenReturn("fake2");

    TestClassLoader frenchPackClassLoader = new TestClassLoader(getClass().getClassLoader().getResource("FrenchPlugin.jar"));
    LanguagePack frenchPack = (LanguagePack) frenchPackClassLoader.loadClass(FRENCH_PACK_CLASS_NAME).newInstance();

    TestClassLoader quebecPackClassLoader = new TestClassLoader(getClass().getClassLoader().getResource("QuebecPlugin.jar"));
    LanguagePack quebecPack = (LanguagePack) quebecPackClassLoader.loadClass(QUEBEC_PACK_CLASS_NAME).newInstance();

    manager = new I18nManager(pluginRepository, new LanguagePack[] { frenchPack, quebecPack });
    manager.start();
  }

  private static URL classSource = I18nManagerTest.class.getProtectionDomain().getCodeSource().getLocation();
  
  public static class TestClassLoader extends URLClassLoader {

    public TestClassLoader(URL url) {      
      super(new URL[] { url, classSource }, Thread.currentThread().getContextClassLoader());
    }

    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
      Class c = findLoadedClass(name);
      if (c == null) {
        if (name.equals(TEST_PLUGIN_CLASS_NAME) || name.equals(QUEBEC_PACK_CLASS_NAME) || name.equals(FRENCH_PACK_CLASS_NAME)) {
          c = findClass(name);
        } else {
          return super.loadClass(name, resolve);
        }
      }
      if (resolve) {
        resolveClass(c);
      }
      return c;
    }
  };
  
  @Test
  public void translateWithoutRegionalVariant()
  {    
    List<String> sentence = Arrays.asList("it", "is", "cold"); 
    String result = "";
    for (String token : sentence)
    {
      result += manager.translation(Locale.FRENCH, token, token) + " ";
    }
    assertEquals("Il fait froid ", result);    
  }
  
  @Test
  public void translateWithRegionalVariant()
  {    
    // it & is are taken from the french language pack
    // and cold is taken from the quebec language pack
    List<String> sentence = Arrays.asList("it", "is", "cold"); 
    String result = "";
    for (String token : sentence)
    {
      result += manager.translation(Locale.CANADA_FRENCH, token, token) + " ";
    }
    assertEquals("Il fait frette ", result);    
  }
  
  @Test
  public void translateReturnsDefaultBundleValue()
  {    
    String result = manager.translation(Locale.FRENCH, "only.english", "Default");
    assertEquals("Ketchup", result);    
  }  

  @Test
  public void translateUnknownValue()
  {    
    String result = manager.translation(Locale.FRENCH, "unknown", "Default value for Unknown");
    assertEquals("Default value for Unknown", result);
    assertEquals(1, manager.getUnknownKeys().size());
    assertEquals("Default value for Unknown", manager.getUnknownKeys().getProperty("unknown"));
  }  
}
