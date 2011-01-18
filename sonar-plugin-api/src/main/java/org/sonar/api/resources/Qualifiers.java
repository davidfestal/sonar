/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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
package org.sonar.api.resources;

/**
 * The qualifier determines the exact type of a resource.
 * Plugins can use their own qualifiers.
 * @since 2.6
 */
public interface Qualifiers {

  String VIEW = "VW";
  String SUBVIEW = "SVW";

  /**
   * Library, for example a JAR dependency of Java projects.
   * Scope is Scopes.PROJECT
   */
  String LIBRARY = "LIB";

  /**
   * Single project or root of multi-modules projects
   * Scope is Scopes.PROJECT
   */
  String PROJECT = "TRK";

  /**
   * Module of multi-modules project. It's sometimes called sub-project.
   * Scope is Scopes.PROJECT
   */
  String MODULE = "BRC";


  String PACKAGE = "PAC";
  String DIRECTORY = "DIR";
  String FILE = "FIL";
  String CLASS = "CLA";
  String METHOD = "MET";
  String FIELD = "FLD";
  
  // ugly, should be replaced by a nature
  String UNIT_TEST_CLASS = "UTS";
}