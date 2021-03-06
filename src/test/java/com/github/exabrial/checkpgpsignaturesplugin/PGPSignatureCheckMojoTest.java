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

import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.exabrial.checkpgpsignaturesplugin.gpg.ExecutionResult;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.AscArtifactResolver;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.DependenciesLocator;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.KeyIdResolver;
import com.github.exabrial.checkpgpsignaturesplugin.model.SignatureCheckFailedException;

@ExtendWith(MockitoExtension.class)
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
	private MojoProperties mojoProperties;
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

	@Test
	public void testExecute_missingMapping() throws Exception {
		final Executable executable = () -> {
			final HashSet<Artifact> artifacts = new HashSet<>();
			final Artifact projectArtifact = mock(Artifact.class);
			artifacts.add(projectArtifact);
			when(dependenciesLocator.getArtifactsToVerify()).thenReturn(artifacts);
			pgpSignatureCheckMojo.execute();
		};
		assertThrows(MojoExecutionException.class, executable);
	}

	@Test
	public void testExecute_missingSignature() throws Exception {
		final Executable executable = () -> {
			final HashSet<Artifact> artifacts = new HashSet<>();
			final Artifact projectArtifact = mock(Artifact.class);
			artifacts.add(projectArtifact);
			when(dependenciesLocator.getArtifactsToVerify()).thenReturn(artifacts);
			when(pgpKeyIdResolver.resolveKeyIdFor(projectArtifact)).thenReturn(keyId);
			pgpSignatureCheckMojo.execute();
		};
		assertThrows(MojoExecutionException.class, executable);
	}

	@Test
	public void testExecute_sigFail() throws Exception {
		final Executable executable = () -> {
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
		};

		assertThrows(MojoFailureException.class, executable);
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
	};
}
