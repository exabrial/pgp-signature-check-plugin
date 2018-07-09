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
 */ com.github.exabrial.checkpgpsignaturesplugin.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;

import javax.inject.Provider;

import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.repository.RepositoryManager;
import org.codehaus.plexus.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.exabrial.checkpgpsignaturesplugin.model.PGPKey;
import com.google.common.io.Files;

@RunWith(MockitoJUnitRunner.class)
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
	private Provider<String> keyCacheDirectory;

	private File parentDirectory;
	private String parentDirectoryName;

	@Before
	public void before() {
		parentDirectory = Files.createTempDir();
		parentDirectoryName = parentDirectory.getAbsolutePath();
		when(keyCacheDirectory.get()).thenReturn(parentDirectoryName);
	}

	@After
	public void after() {
		FileUtils.deleteQuietly(new File(parentDirectoryName));
	}

	@Test
	public void testPostConstruct() throws Exception {
		when(keyCacheDirectory.get()).thenReturn(null);
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
	public void testPostConstruct_specificDirExists() throws Exception {
		fileKeysCache.postConstruct();
		verify(logger).info("postConstruct() using existing keyCacheDirectory:" + parentDirectory.getAbsolutePath());
	}

	@Test(expected = UserSpecifiedKeyCacheDirectoryDoesntExistException.class)
	public void testPostConstruct_specificDoesntExist() throws Exception {
		parentDirectory.delete();
		assertFalse(new File(parentDirectoryName).exists());
		fileKeysCache.postConstruct();
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
