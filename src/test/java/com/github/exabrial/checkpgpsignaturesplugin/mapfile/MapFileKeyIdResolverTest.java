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

package com.github.exabrial.checkpgpsignaturesplugin.mapfile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.exabrial.checkpgpsignaturesplugin.MojoProperties;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.KeyIdResolver;
import com.github.exabrial.checkpgpsignaturesplugin.model.InvalidPGPKeyIdException;
import com.google.common.io.Files;

@ExtendWith(MockitoExtension.class)
public class MapFileKeyIdResolverTest {
	@InjectMocks
	private MapFileKeyIdResolver mapFileKeyIdResolver;
	@Mock
	private KeyIdResolver pgpKeyIdResolver;
	@Mock
	private MojoProperties mojoProperties;
	@Mock
	private MavenProject project;
	@Mock
	private Logger logger;
	private File projectBaseDir;

	@BeforeEach
	public void before() throws Exception {
		projectBaseDir = Files.createTempDir();
		when(project.getBasedir()).thenReturn(projectBaseDir);
	}

	private void copyMapFile(final String fileName) throws Exception {
		FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/artifact-key-map.txt"),
				new File(projectBaseDir, fileName != null ? fileName : "artifact-key-map.txt"));
	}

	private void insertBadLine() throws Exception {
		FileUtils.writeStringToFile(new File(projectBaseDir, "artifact-key-map.txt"), "org.junit.*:*:*=0xNotAValidKeyId",
				StandardCharsets.UTF_8, true);
	}

	@AfterEach
	public void after() throws Exception {
		FileUtils.deleteQuietly(projectBaseDir);
	}

	@Test
	public void testPostConstruct_suppliedName_testResolveKeyIdFor() throws Exception {
		final String testFile = "TEST_FILE";
		copyMapFile(testFile);
		when(mojoProperties.getProperty("keyMapFileName")).thenReturn(testFile);
		mapFileKeyIdResolver.postConstruct();
		final Artifact artifact = new DefaultArtifact("org.junit.jupiter", "junit-jupiter-api", "5.2.0", "test", "jar", null,
				mock(ArtifactHandler.class));
		assertEquals("FF6E2C001948C5F2F38B0CC385911F425EC61B51", mapFileKeyIdResolver.resolveKeyIdFor(artifact));
	}

	@Test
	public void testPostConstruct_missingMapFile() throws Exception {
		final Executable executable = () -> {
			mapFileKeyIdResolver.postConstruct();
		};
		assertThrows(MissingKeyMapFileException.class, executable);
	}

	@Test
	public void testResolveKeyIdFor_null() throws Exception {
		copyMapFile(null);
		mapFileKeyIdResolver.postConstruct();
		final Artifact artifact = new DefaultArtifact("DOESNOTEXIST", "DOESNOTEXIST", "5.2.0", "test", "jar", null,
				mock(ArtifactHandler.class));
		assertNull(mapFileKeyIdResolver.resolveKeyIdFor(artifact));
	}

	@Test
	public void testResolveKeyIdFor_badKeyId() throws Exception {
		final Executable executable = () -> {
			copyMapFile(null);
			insertBadLine();
			mapFileKeyIdResolver.postConstruct();
		};
		assertThrows(InvalidPGPKeyIdException.class, executable);
	}

	@Test
	public void testIsVerificationSkipped() throws Exception {
		copyMapFile(null);
		mapFileKeyIdResolver.postConstruct();
		final Artifact artifact = new DefaultArtifact("org.junit.jupiter", "junit-jupiter-api", "5.2.0", "runtime", "jar", null,
				mock(ArtifactHandler.class));
		assertFalse(mapFileKeyIdResolver.isVerificationSkipped(artifact));
	}

	@Test
	public void testIsVerificationSkipped_dontSkipMissing() throws Exception {
		copyMapFile(null);
		mapFileKeyIdResolver.postConstruct();
		final Artifact artifact = new DefaultArtifact("DOESNOTEXIST", "DOESNOTEXIST", "5.2.0", "test", "jar", null,
				mock(ArtifactHandler.class));
		assertFalse(mapFileKeyIdResolver.isVerificationSkipped(artifact));
	}

	@Test
	public void testIsVerificationSkipped_skipped() throws Exception {
		copyMapFile(null);
		mapFileKeyIdResolver.postConstruct();
		final Artifact artifact = new DefaultArtifact("com.skip.me", "please", "5.2.0", "compile", "jar", null,
				mock(ArtifactHandler.class));
		assertTrue(mapFileKeyIdResolver.isVerificationSkipped(artifact));
	}
}
