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
package org.sonar.api.platform;

import org.sonar.api.ServerComponent;

/**
 * @since 2.5
 */
public interface ServerUpgradeStatus extends ServerComponent {

  /**
   * Has the database been upgraded during the current startup ? Return false if isInstalledFromScratch() is true.
   */
  boolean isUpgraded();

  /**
   * Has the database been created from scratch during the current startup ?
   */
  boolean isFreshInstall();

  /**
   * The database version before the server startup. Returns <=0 if db created from scratch.
   */
  int getInitialDbVersion();
  
}
