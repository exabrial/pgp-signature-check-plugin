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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.artifact.filter.StrictPatternIncludesArtifactFilter;
import org.codehaus.plexus.logging.Logger;

import com.github.exabrial.checkpgpsignaturesplugin.MojoProperties;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.KeyIdResolver;
import com.github.exabrial.checkpgpsignaturesplugin.model.InvalidPGPKeyIdException;

@Named
@Singleton
public class MapFileKeyIdResolver implements KeyIdResolver {
	private static final Pattern keyIdPattern = Pattern.compile("^0x[a-f0-9]{16,40}$", Pattern.CASE_INSENSITIVE);
	private final Map<StrictPatternIncludesArtifactFilter, String> artifactKeyMap = new HashMap<>();
	private final Map<StrictPatternIncludesArtifactFilter, String> artifactSkipMap = new HashMap<>();
	@Inject
	private MojoProperties mojoProperties;
	@Inject
	private Logger logger;

	@PostConstruct
	void postConstruct() {
		final String keyMapFileName = mojoProperties.getProperty("keyMapFileName");
		final File keyMapFile = new File(keyMapFileName);
		if (!keyMapFile.exists()) {
			throw new MissingKeyMapFileException(keyMapFile);
		} else {
			try {
				logger.info("postConstruct() using keyMapFile:" + keyMapFile.getAbsolutePath());
				final List<String> lines = Files.readAllLines(Paths.get(keyMapFile.getAbsolutePath()));
				lines.stream().forEach(line -> parse(line.trim()));
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public String resolveKeyIdFor(final Artifact artifact) {
		final String keyId = artifactKeyMap.keySet().stream().filter(artifactPattern -> artifactPattern.include(artifact)).findFirst()
				.map(artifactKeyMap::get).orElse(null);
		logger.debug("resolveKeyIdFor() artifact:" + artifact + " mapped to keyId:" + keyId);
		return keyId;
	}

	@Override
	public boolean isVerificationSkipped(final Artifact artifact) {
		final String keyId = artifactSkipMap.keySet().stream().filter(artifactPattern -> artifactPattern.include(artifact)).findFirst()
				.map(artifactSkipMap::get).orElse(null);
		return keyId != null;
	}

	private void parse(final String line) {
		if (!line.equals("") && !line.startsWith("#")) {
			final String[] values = line.split("=");
			final StrictPatternIncludesArtifactFilter filter = new StrictPatternIncludesArtifactFilter(Arrays.asList(values[0]));
			if ("skip-signature-check".equals(values[1])) {
				artifactSkipMap.put(filter, "skip-signature-check");
			} else {
				checkKeyId(values[1]);
				artifactKeyMap.put(filter, values[1].substring(2));
			}
		}
	}

	private static void checkKeyId(final String keyId) {
		if (!keyIdPattern.matcher(keyId).matches()) {
			throw new InvalidPGPKeyIdException(keyId, keyIdPattern);
		}
	}
}
