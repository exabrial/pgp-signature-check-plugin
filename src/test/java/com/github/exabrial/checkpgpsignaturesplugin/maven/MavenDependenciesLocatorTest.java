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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.exabrial.checkpgpsignaturesplugin.MojoProperties;
import com.github.exabrial.checkpgpsignaturesplugin.model.NoProjectArtifactFoundException;

@ExtendWith(MockitoExtension.class)
public class MavenDependenciesLocatorTest {
	@InjectMocks
	private MavenDependenciesLocator mavenDependenciesLocator;
	@Mock
	private MavenProject mavenProject;
	@Mock
	private RepositorySystem repositorySystem;
	@Mock
	private ArtifactRepository localRepository;
	@Mock
	private List<ArtifactRepository> remoteRepositories;
	@Mock
	private ArtifactResolutionResult artifactResolutionResult;
	@Mock
	private MojoProperties mojoProperties;
	private final Set<Artifact> artifacts = new HashSet<>(Arrays.asList(new Artifact[] { mock(Artifact.class) }));
	private final Set<Artifact> pomArtifacts = new HashSet<>(Arrays.asList(new Artifact[] { mock(Artifact.class) }));

	@BeforeEach
	public void before() throws Exception {
		when(mavenProject.getArtifacts()).thenReturn(artifacts);
	}

	private void mockRepo() {
		when(artifactResolutionResult.getArtifacts()).thenReturn(pomArtifacts);
		when(repositorySystem.resolve(any(ArtifactResolutionRequest.class))).thenReturn(artifactResolutionResult);
	}

	@Test
	public void testGetArtifactsToVerify() throws Exception {
		when(mojoProperties.getProperty("checkPomSignatures")).thenReturn(String.valueOf(true));
		mockRepo();
		final HashSet<Object> combined = new HashSet<>();
		combined.addAll(artifacts);
		combined.addAll(pomArtifacts);
		assertEquals(combined, mavenDependenciesLocator.getArtifactsToVerify());
	}

	@Test
	public void testGetArtifactsToVerify_noPoms() throws Exception {
		when(mojoProperties.getProperty("checkPomSignatures")).thenReturn(String.valueOf(false));
		assertEquals(artifacts, mavenDependenciesLocator.getArtifactsToVerify());
	}

	@Test
	public void testGetArtifactsToVerify_missingProject() throws Exception {
		final Executable executable = () -> {
			mockRepo();
			when(mojoProperties.getProperty("checkPomSignatures")).thenReturn(String.valueOf(true));
			when(artifactResolutionResult.getArtifacts()).thenReturn(new HashSet<>());
			mavenDependenciesLocator.getArtifactsToVerify();
		};
		assertThrows(NoProjectArtifactFoundException.class, executable);
	}
}
