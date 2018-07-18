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

package com.github.exabrial.checkpgpsignaturesplugin.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.repository.RepositoryManager;
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
import com.github.exabrial.checkpgpsignaturesplugin.model.PGPKey;
import com.google.common.io.Files;

@ExtendWith(MockitoExtension.class)
public class FileKeysCacheTest {
	@InjectMocks
	private FileKeysCache fileKeysCache;
	@Mock
	private Logger logger;
	@Mock
	protected RepositoryManager repositoryManager;
	@Mock
	private MavenSession mavenSession;
	@Mock
	private MojoProperties mojoProperties;

	private File parentDirectory;
	private String parentDirectoryName;

	@BeforeEach
	public void before() {
		parentDirectory = Files.createTempDir();
		parentDirectoryName = parentDirectory.getAbsolutePath();
		when(mojoProperties.getProperty("keyCacheDirectory")).thenReturn(parentDirectoryName);
	}

	@AfterEach
	public void after() {
		FileUtils.deleteQuietly(new File(parentDirectoryName));
	}

	@Test
	public void testPostConstruct() throws Exception {
		when(mojoProperties.getProperty("keyCacheDirectory")).thenReturn(null);
		final ProjectBuildingRequest projectBuildingRequest = mock(ProjectBuildingRequest.class);
		when(mavenSession.getProjectBuildingRequest()).thenReturn(projectBuildingRequest);
		final File repoDirectory = new File(parentDirectory, "repository");
		repoDirectory.mkdir();
		assertFalse(new File(parentDirectory, "artifactPubKeys").exists());
		when(repositoryManager.getLocalRepositoryBasedir(projectBuildingRequest)).thenReturn(repoDirectory);
		fileKeysCache.postConstruct();
		assertTrue(new File(parentDirectory, "artifactPubKeys").exists());
	}

	@Test
	public void testPostConstruct_artifactPubKeysAlreadyExists() throws Exception {
		when(mojoProperties.getProperty("keyCacheDirectory")).thenReturn(null);
		final ProjectBuildingRequest projectBuildingRequest = mock(ProjectBuildingRequest.class);
		when(mavenSession.getProjectBuildingRequest()).thenReturn(projectBuildingRequest);
		final File repoDirectory = new File(parentDirectory, "repository");
		repoDirectory.mkdir();
		final File artifactPubKeys = new File(parentDirectory, "artifactPubKeys");
		artifactPubKeys.mkdir();
		assertTrue(artifactPubKeys.exists());
		when(repositoryManager.getLocalRepositoryBasedir(projectBuildingRequest)).thenReturn(repoDirectory);
		fileKeysCache.postConstruct();
		assertTrue(new File(parentDirectory, "artifactPubKeys").exists());
	}

	@Test
	public void testPostConstruct_specificDirExists() throws Exception {
		fileKeysCache.postConstruct();
		verify(logger).info("postConstruct() using existing keyCacheDirectory:" + parentDirectory.getAbsolutePath());
	}

	@Test
	public void testPostConstruct_specificDoesntExist() throws Exception {
		final Executable executable = () -> {
			parentDirectory.delete();
			assertFalse(new File(parentDirectoryName).exists());
			fileKeysCache.postConstruct();
		};
		assertThrows(UserSpecifiedKeyCacheDirectoryDoesntExistException.class, executable);
	}

	@Test
	public void testGetKeyFile() throws Exception {
		fileKeysCache.postConstruct();
		final File keyIdFile = new File(parentDirectory, "keyId.kbx");
		keyIdFile.createNewFile();
		final File cachedKeyFile = fileKeysCache.getKeyFile("keyId");
		assertEquals(keyIdFile, cachedKeyFile);
		// Now that we're in the cache, test to make sure it's not creating a new file
		// each time
		assertTrue(cachedKeyFile == fileKeysCache.getKeyFile("keyId"));
	}

	@Test
	public void testGetKeyFile_doesntExist() throws Exception {
		fileKeysCache.postConstruct();
		assertNull(fileKeysCache.getKeyFile("keyId"));
	}

	@Test
	public void testPut() throws Exception {
		fileKeysCache.postConstruct();
		final PGPKey pgpKey = new PGPKey("keyId", new byte[] { 1, 2, 3 });
		fileKeysCache.put(pgpKey);
		final File keyIdFile = new File(parentDirectory, "keyId.kbx");
		assertTrue(keyIdFile.exists());
		try (final RandomAccessFile keyIdRAF = new RandomAccessFile(keyIdFile, "r")) {
			final byte[] contents = new byte[(int) keyIdRAF.length()];
			keyIdRAF.readFully(contents);
			assertTrue(Arrays.equals(pgpKey.keyData, contents));
		}
	}
}
