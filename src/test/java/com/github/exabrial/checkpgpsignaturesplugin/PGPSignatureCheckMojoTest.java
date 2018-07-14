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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.HashSet;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.exabrial.checkpgpsignaturesplugin.gpg.ExecutionResult;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.AscArtifactResolver;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.DependenciesLocator;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.KeyIdResolver;
import com.github.exabrial.checkpgpsignaturesplugin.model.SignatureCheckFailedException;

@RunWith(MockitoJUnitRunner.class)
public class PGPSignatureCheckMojoTest {
	@InjectMocks
	private PGPSignatureCheckMojo pgpSignatureCheckMojo;
	@Mock
	private ArtifactChecker artifactChecker;
	@Mock
	private AscArtifactResolver ascArtifactResolver;
	@Mock
	private DependenciesLocator dependenciesLocator;
	@Mock
	private KeyIdResolver pgpKeyIdResolver;
	@Mock
	private Logger logger;
	private final String keyId = "keyId0";

	@Test
	public void testExecute() throws Exception {
		final HashSet<Artifact> artifacts = new HashSet<>();
		final Artifact projectArtifact = mock(Artifact.class);
		artifacts.add(projectArtifact);
		when(dependenciesLocator.getArtifactsToVerify()).thenReturn(artifacts);
		when(pgpKeyIdResolver.resolveKeyIdFor(projectArtifact)).thenReturn(keyId);
		final Artifact ascArtifact = mock(Artifact.class);
		when(ascArtifactResolver.resolveAscArtifact(projectArtifact)).thenReturn(ascArtifact);
		pgpSignatureCheckMojo.execute();
		verify(artifactChecker).check(projectArtifact, ascArtifact, keyId);
	}

	@Test(expected = MojoExecutionException.class)
	public void testExecute_noSignature() throws Exception {
		final HashSet<Artifact> artifacts = new HashSet<>();
		final Artifact projectArtifact = mock(Artifact.class);
		artifacts.add(projectArtifact);
		when(dependenciesLocator.getArtifactsToVerify()).thenReturn(artifacts);
		pgpSignatureCheckMojo.execute();
	}

	@Test(expected = MojoFailureException.class)
	public void testExecute_sigFail() throws Exception {
		final HashSet<Artifact> artifacts = new HashSet<>();
		final Artifact projectArtifact = mock(Artifact.class);
		artifacts.add(projectArtifact);
		when(dependenciesLocator.getArtifactsToVerify()).thenReturn(artifacts);
		when(pgpKeyIdResolver.resolveKeyIdFor(projectArtifact)).thenReturn(keyId);
		final Artifact ascArtifact = mock(Artifact.class);
		when(ascArtifactResolver.resolveAscArtifact(projectArtifact)).thenReturn(ascArtifact);
		doThrow(new SignatureCheckFailedException(mock(ExecutionResult.class), keyId, new File("/tmp"))).when(artifactChecker)
		.check(projectArtifact, ascArtifact, keyId);
		pgpSignatureCheckMojo.execute();
	}

	@Test
	public void testExecute_skipVerification() throws Exception {
		final HashSet<Artifact> artifacts = new HashSet<>();
		final Artifact projectArtifact = mock(Artifact.class);
		artifacts.add(projectArtifact);
		when(dependenciesLocator.getArtifactsToVerify()).thenReturn(artifacts);
		when(pgpKeyIdResolver.isVerificationSkipped(projectArtifact)).thenReturn(true);
		pgpSignatureCheckMojo.execute();
		verifyNoMoreInteractions(artifactChecker);
	}
}
