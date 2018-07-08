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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.repository.RepositoryManager;
import org.codehaus.plexus.logging.Logger;
import org.eclipse.sisu.Nullable;

import com.github.exabrial.checkpgpsignaturesplugin.interfaces.KeysCache;
import com.github.exabrial.checkpgpsignaturesplugin.model.PGPKey;

@Named
@Singleton
public class FileKeysCache implements KeysCache {
	private final Map<String, File> keyCache = new HashMap<>();
	@Inject
	private Logger logger;
	@Inject
	protected RepositoryManager repositoryManager;
	@Inject
	private MavenSession mavenSession;
	@Inject
	@Nullable
	@Named("${keyCacheDirectory}")
	private String keyCacheDirectory;

	@PostConstruct
	public void postConstruct() {
		if (keyCacheDirectory == null) {
			final ProjectBuildingRequest projectBuildingRequest = mavenSession.getProjectBuildingRequest();
			final File repoDirectory = repositoryManager.getLocalRepositoryBasedir(projectBuildingRequest);
			final File m2Directory = repoDirectory.getParentFile();
			keyCacheDirectory = m2Directory.getAbsolutePath() + File.separator + "artifactPubKeys";
		}
		final File keyCacheDirectoryFile = new File(keyCacheDirectory);
		if (!keyCacheDirectoryFile.exists()) {
			logger.info("postConstruct() creating keyCacheDirectory:" + keyCacheDirectory);
			keyCacheDirectoryFile.mkdir();
		} else {
			logger.info("postConstruct() using existing keyCacheDirectory:" + keyCacheDirectory);
		}
	}

	@Override
	public File getKeyLocation(final String requiredKeyId) {
		if (keyCache.containsKey(requiredKeyId)) {
			logger.debug("getKeyLocation() cache hit requiredKeyId:" + requiredKeyId);
			return keyCache.get(requiredKeyId);
		} else {
			final File keyFile = new File(keyCacheDirectory + File.separator + requiredKeyId + ".kbx");
			if (keyFile.exists()) {
				logger.debug("getKeyLocation() cache miss, but exists on disk requiredKeyId:" + requiredKeyId);
				keyCache.put(requiredKeyId, keyFile);
				return keyFile;
			} else {
				logger.debug("getKeyLocation() cache miss, do not posess requiredKeyId:" + requiredKeyId);
				return null;
			}
		}
	}

	@Override
	public File put(final PGPKey pgpKey) {
		logger.debug("put() writing to disk pgpKey:" + pgpKey);
		final File keyFile = new File(keyCacheDirectory + File.separator + pgpKey.keyId + ".kbx");
		try {
			if (keyFile.exists()) {
				logger.debug("put() deleting existing file");
				keyFile.delete();
			}
			keyFile.createNewFile();
			try (FileOutputStream fos = new FileOutputStream(keyFile)) {
				logger.debug("put() writing data....");
				fos.write(pgpKey.keyData);
			}
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		logger.debug("put() returning keyFile:" + keyFile);
		return keyFile;
	}
}
