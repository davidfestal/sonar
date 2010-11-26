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
package org.sonar.batch.index;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.database.model.Snapshot;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.utils.SonarException;
import org.sonar.jpa.test.AbstractDbUnitTestCase;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SourcePersisterTest extends AbstractDbUnitTestCase {

  private SourcePersister sourcePersister;

  @Before
  public void before() {
    setupData("shared");
    Snapshot snapshot = getSession().getSingleResult(Snapshot.class, "id", 1000);
    ResourcePersister resourcePersister = mock(ResourcePersister.class);
    when(resourcePersister.saveResource((Project) anyObject(), (Resource) anyObject())).thenReturn(snapshot);
    sourcePersister = new SourcePersister(getSession(), resourcePersister);
  }

  @Test
  public void shouldSaveSource() {
    sourcePersister.saveSource(new Project(""), new JavaFile("org.foo.Bar"), "this is the file content");
    checkTables("shouldSaveSource", "snapshot_sources");
  }

  @Test(expected = SonarException.class)
  public void shouldFailIfSourceSavedSeveralTimes() {
    Project project = new Project("project");
    JavaFile file = new JavaFile("org.foo.Bar");
    sourcePersister.saveSource(project, file, "this is the file content");
    sourcePersister.saveSource(project, file, "new content"); // fail
  }
}