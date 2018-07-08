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

package com.github.exabrial.checkpgpsignaturesplugin.repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryAscArtifactResolverTest {
	@InjectMocks
	private RepositoryAscArtifactResolver repositoryAscArtifactResolver;
	@Mock
	private RepositorySystem repositorySystem;
	@Mock
	private MavenSession session;
	@Mock
	private MavenProject project;
	@Mock
	private ArtifactResolutionResult ascResult;
	@Mock
	private Artifact artifact;

	@Before
	public void before() {
		final MavenExecutionRequest mavenExecutionRequest = mock(MavenExecutionRequest.class);
		when(session.getRequest()).thenReturn(mavenExecutionRequest);
		final Artifact ascArtifact = mock(Artifact.class);
		when(repositorySystem.createArtifactWithClassifier(isNull(), isNull(), isNull(), isNull(), isNull())).thenReturn(ascArtifact);
		when(repositorySystem.resolve(any())).thenReturn(ascResult);
	}

	@Test
	public void testResolveAscArtifact() {
		final HashSet<Artifact> artifacts = new HashSet<>();
		final Artifact ascArtifact = mock(Artifact.class);
		artifacts.add(ascArtifact);
		when(ascResult.getArtifacts()).thenReturn(artifacts);
		assertEquals(ascArtifact, repositoryAscArtifactResolver.resolveAscArtifact(artifact));
	}

	@Test
	public void testResolveAscArtifact_missingAsc() {
		assertNull(repositoryAscArtifactResolver.resolveAscArtifact(artifact));
	}
}
