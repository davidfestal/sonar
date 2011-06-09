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

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.collections.EnumerationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.Plugin;
import org.sonar.api.ServerExtension;
import org.sonar.api.i18n.I18n;
import org.sonar.api.i18n.LanguagePack;
import org.sonar.api.utils.SonarException;
import org.sonar.core.plugin.AbstractPluginRepository;

/**
 * @author david
 * 
 */
public class I18nManager implements I18n, ServerExtension, BatchExtension {

  private static final Logger LOG = LoggerFactory.getLogger(I18nManager.class);
  public static final String packagePathToSearchIn = "org/sonar/i18n";

  private AbstractPluginRepository pluginProvider;
  private LanguagePack[] languagePacks;

  public I18nManager(AbstractPluginRepository pluginProvider, LanguagePack[] languagePacks) {
    this.pluginProvider = pluginProvider;
    this.languagePacks = languagePacks;
  }

  private Map<String, String> keys = new HashMap<String, String>();
  private Properties unknownKeys = new Properties();

  private class BundleClassLoader extends URLClassLoader {

    private Map<String, ClassLoader> resources = new HashMap<String, ClassLoader>();

    public BundleClassLoader() {
      super(new URL[] {}, null);
    }

    public void addResource(String resourceName, ClassLoader classloader) {
      resources.put(resourceName, classloader);
    }

    @Override
    public URL findResource(String name) {
      if (resources.containsKey(name)) {
        return resources.get(name).getResource(name);
      }
      return null;
    }
  };

  private BundleClassLoader bundleClassLoader = new BundleClassLoader();

  public void start() {
    try {
      LOG.info("Start the I18nManager server component");
      Set<URI> alreadyLoadedResources = new HashSet<URI>();

      Collection<Plugin> plugins = pluginProvider.getPlugins();
      for (Plugin plugin : plugins) {
        searchAndStoreBundleNames(pluginProvider.getPluginKey(plugin), plugin.getClass().getClassLoader(), alreadyLoadedResources);
      }
      for (LanguagePack pack : languagePacks) {
        addLanguagePack(pack);
      }
    } catch (Exception e) {
      LOG.error("Error during I18nManager component start", e);
    }
  }

  private void addLanguagePack(LanguagePack languagePack) {
    LOG.debug("Search for language bundles in language pack : {}", languagePack.toString());
    for (String pluginKey : languagePack.getPluginKeys()) {
      String bundleBaseName = buildBundleBaseName(pluginKey);
      for (Locale locale : languagePack.getLocales()) {
        String bundlePropertiesFile = new StringBuilder(bundleBaseName).append('_').append(locale.toString()).append(".properties")
            .toString();
        ClassLoader classloader = languagePack.getClass().getClassLoader();
        LOG.info("Adding locale {} for bundleName : {} from classloader : {}", new Object[] { locale, bundleBaseName, classloader });
        bundleClassLoader.addResource(bundlePropertiesFile, classloader);
      }
    }
  }

  private String buildBundleBaseName(String pluginKey) {
    return packagePathToSearchIn + "/" + pluginKey;
  }

  private void searchAndStoreBundleNames(String pluginKey, ClassLoader classloader, Set<URI> alreadyLoadedResources) {
    String bundleBaseName = buildBundleBaseName(pluginKey);
    String bundleDefaultPropertiesFile = bundleBaseName + ".properties";
    try {
      LOG.debug("Search for ResourceBundle base file '" + bundleDefaultPropertiesFile + "' in the classloader : " + classloader);
      List<URL> resources = EnumerationUtils.toList(classloader.getResources(bundleDefaultPropertiesFile));
      if (resources.size() > 0) {
        if (resources.size() > 1) {
          LOG.warn("File '{}' found several times in the classloader : {}. Only the first one will be taken in account.",
              bundleDefaultPropertiesFile, classloader);
        }

        URL propertiesUrl = resources.get(0);
        if ( !alreadyLoadedResources.contains(propertiesUrl.toURI())) {
          LOG.debug("Found the ResourceBundle base file : {} from classloader : {}", propertiesUrl, classloader);
          LOG.info("Add bundleName : {} from classloader : {}", bundleBaseName, classloader);
          bundleClassLoader.addResource(bundleDefaultPropertiesFile, classloader);
          alreadyLoadedResources.add(propertiesUrl.toURI());

          Properties bundleContent = new Properties();
          Reader reader = null;
          try {
            reader = new InputStreamReader(propertiesUrl.openStream());
            bundleContent.load(reader);
            Enumeration<String> keysToAdd = (Enumeration<String>) bundleContent.propertyNames();
            while (keysToAdd.hasMoreElements()) {
              String key = keysToAdd.nextElement();
              if (keys.containsKey(key)) {
                LOG.warn("DUPLICATE KEY : Key '{}' defined in bundle '{}' is already defined in bundle '{}'. It is ignored.", new Object[] {
                    key, bundleBaseName, keys.get(key) });
              } else {
                keys.put(key, bundleBaseName);
              }
            }
          } finally {
            if (reader != null) {
              reader.close();
            }
          }
        }
      }
    } catch (Exception e) {
      LOG.error("Fail to load '" + bundleDefaultPropertiesFile + "' in classloader : " + classloader, e);
      throw new SonarException("Fail to load '" + bundleDefaultPropertiesFile + "' in classloader : " + classloader, e);
    }
  }

  public String translation(final Locale locale, final String key, final String defaultText, final Object... objects) {
    String result = defaultText;
    try {
      String bundleBaseName = keys.get(key);
      if (bundleBaseName == null) {
        LOG.warn("UNKNOWN KEY : Key '{}' not found in any bundle. Default value '{}' is returned.", key, defaultText);
        unknownKeys.put(key, defaultText);
      } else {
        try {
          ResourceBundle bundle = null;
          bundle = ResourceBundle.getBundle(bundleBaseName, locale, bundleClassLoader);

          String value = bundle.getString(key);
          if ("".equals(value)) {
            LOG.warn("VOID KEY : Key '{}' (from bundle '{}') returns a void value. Default value '{}' is returned.", new Object[] { key,
                bundleBaseName, defaultText });
          } else {
            result = value;
          }
        } catch (MissingResourceException e) {
          LOG.warn("BUNDLE NOT LOADED : Failed loading bundle {} from classloader {}. Default value '{}' is returned.", new Object[] {
              bundleBaseName, bundleClassLoader, defaultText });
        }
      }
    } catch (Exception e) {
      LOG.error("Exception when retrieving I18n string.", e);
    }

    if (objects.length > 0) {
      LOG.debug("Translation : {}, {}, {}, {}", new String[] { locale.toString(), key, defaultText, Arrays.deepToString(objects) });
      return MessageFormat.format(result, objects);
    } else {
      return result;
    }
  }

  /**
   * @return the unknownKeys
   */
  public Properties getUnknownKeys() {
    return unknownKeys;
  }
}
