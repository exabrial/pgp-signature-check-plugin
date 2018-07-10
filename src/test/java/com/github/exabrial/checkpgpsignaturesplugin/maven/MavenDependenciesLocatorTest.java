/**
 * Copyright [2018] [Jonathan S. Fisher]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.exabrial.checkpgpsignaturesplugin.maven;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.ProjectDependenciesResolver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({ "deprecation" })
public class MavenDependenciesLocatorTest {
	@InjectMocks
	private MavenDependenciesLocator mavenDependenciesLocator;
	@Mock
	private MavenSession mavenSession;
	@Mock
	private MavenProject mavenProject;
	@Mock
	private ProjectDependenciesResolver projectDependenciesResolver;

	@Test
	public void testGetArtifactsToVerify() throws Exception {
		final HashSet<Artifact> expected = new HashSet<>();
		when(projectDependenciesResolver.resolve(eq(mavenProject), anyCollection(), eq(mavenSession))).thenReturn(expected);
		final Set<Artifact> artifactsToVerify = mavenDependenciesLocator.getArtifactsToVerify();
		assertEquals(expected, artifactsToVerify);
	}

	@Test(expected = RuntimeException.class)
	public void testGetArtifactsToVerify_err() throws Exception {
		when(projectDependenciesResolver.resolve(eq(mavenProject), anyCollection(), eq(mavenSession))).thenThrow(new IOException());
		mavenDependenciesLocator.getArtifactsToVerify();
	}
}
