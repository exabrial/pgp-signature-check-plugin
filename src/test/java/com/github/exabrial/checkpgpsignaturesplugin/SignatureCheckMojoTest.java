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

package com.github.exabrial.checkpgpsignaturesplugin;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.exabrial.checkpgpsignaturesplugin.interfaces.AscArtifactResolver;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.DependenciesLocator;

@RunWith(MockitoJUnitRunner.class)
public class SignatureCheckMojoTest {
	@InjectMocks
	private SignatureCheckMojo signatureCheckMojo;
	@Mock
	private ArtifactChecker artifactChecker;
	@Mock
	private AscArtifactResolver ascArtifactResolver;
	@Mock
	private DependenciesLocator dependenciesLocator;
	@Mock
	private Logger logger;

	@Test
	public void testExecute() throws Exception {
		final HashSet<Artifact> artifacts = new HashSet<>();
		final Artifact projectArtifact = mock(Artifact.class);
		artifacts.add(projectArtifact);
		when(dependenciesLocator.getArtifactsToVerify()).thenReturn(artifacts);
		final Artifact ascArtifact = mock(Artifact.class);
		when(ascArtifactResolver.resolveAscArtifact(projectArtifact)).thenReturn(ascArtifact);

		signatureCheckMojo.execute();
		verify(artifactChecker).check(projectArtifact, ascArtifact);
	}
}
