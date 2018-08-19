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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.io.FileUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.repository.RepositoryManager;
import org.codehaus.plexus.logging.Logger;

import com.github.exabrial.checkpgpsignaturesplugin.MojoProperties;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.KeysCache;
import com.github.exabrial.checkpgpsignaturesplugin.model.PGPKey;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Named
@Singleton
@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
public class FileKeysCache implements KeysCache {
	private final Map<String, File> keyCache = Collections.synchronizedMap(new HashMap<>());
	@Inject
	private Logger logger;
	@Inject
	protected RepositoryManager repositoryManager;
	@Inject
	private MavenSession mavenSession;
	@Inject
	private MojoProperties mojoProperties;
	private File keyCacheDirectoryFile;

	@PostConstruct
	public void postConstruct() {
		if ("~/.m2/artifactPubKeys".equals(mojoProperties.getProperty("keyCacheDirectory"))) {
			logger.debug("postConstruct() using keycache default directory");
			final ProjectBuildingRequest projectBuildingRequest = mavenSession.getProjectBuildingRequest();
			final File repoDirectory = repositoryManager.getLocalRepositoryBasedir(projectBuildingRequest);
			final File m2Directory = repoDirectory.getParentFile();
			keyCacheDirectoryFile = new File(m2Directory, "artifactPubKeys");
			if (!keyCacheDirectoryFile.exists()) {
				keyCacheDirectoryFile.mkdir();
			}
		} else {
			keyCacheDirectoryFile = new File(mojoProperties.getProperty("keyCacheDirectory"));
			if (!keyCacheDirectoryFile.exists()) {
				throw new UserSpecifiedKeyCacheDirectoryDoesntExistException(keyCacheDirectoryFile);
			} else {
				logger.info("postConstruct() using existing keyCacheDirectory:" + keyCacheDirectoryFile.getAbsolutePath());
			}
		}
	}

	@Override
	public File getKeyFile(final String keyId) {
		if (keyCache.containsKey(keyId)) {
			logger.debug("getKeyLocation() cache hit keyId:" + keyId);
			return keyCache.get(keyId);
		} else {
			final File keyFile = new File(keyCacheDirectoryFile, keyId + ".kbx");
			if (keyFile.exists()) {
				logger.debug("getKeyLocation() cache miss, but exists on disk keyId:" + keyId);
				keyCache.put(keyId, keyFile);
				return keyFile;
			} else {
				keyCache.put(keyId, null);
				logger.debug("getKeyLocation() cache miss, do not posess keyId:" + keyId);
				return null;
			}
		}
	}

	@Override
	public File put(final PGPKey pgpKey) throws IOException {
		logger.debug("put() writing to disk pgpKey:" + pgpKey);
		final File keyFile = new File(keyCacheDirectoryFile, pgpKey.keyId + ".kbx");
		keyFile.createNewFile();
		FileUtils.writeByteArrayToFile(keyFile, pgpKey.keyData);
		logger.debug("put() returning keyFile:" + keyFile);
		return keyFile;
	}
}
