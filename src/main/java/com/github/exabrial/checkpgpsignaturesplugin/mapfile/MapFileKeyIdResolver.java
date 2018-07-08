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
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.StrictPatternIncludesArtifactFilter;
import org.codehaus.plexus.logging.Logger;
import org.eclipse.sisu.Nullable;

import com.github.exabrial.checkpgpsignaturesplugin.interfaces.KeyIdResolver;

@Named
@Singleton
public class MapFileKeyIdResolver implements KeyIdResolver {
	private static final Pattern keyIdPattern = Pattern.compile("^0x[A-Za-z0-9]{16,40}$");
	private final Map<StrictPatternIncludesArtifactFilter, String> artifactKeyMap = new HashMap<>();
	@Inject
	@Nullable
	@Named("${keyMapFileName:-artifact-key-map.txt}")
	private String keyMapFileName;
	@Inject
	private MavenProject project;
	@Inject
	private Logger logger;

	@PostConstruct
	void postConstruct() {
		final File keyMapFile = new File(project.getBasedir().getAbsolutePath() + File.separator + keyMapFileName);
		logger.info("postConstruct() using keyMapFile:" + keyMapFile.getAbsolutePath());
		if (!keyMapFile.exists()) {
			throw new RuntimeException("keyMapFile:" + keyMapFile.getAbsolutePath() + " does not exist");
		} else {
			try {
				final List<String> lines = Files.readAllLines(Paths.get(keyMapFile.getAbsolutePath()));
				lines.stream().forEach(line -> parse(line.trim()));
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void parse(final String line) {
		if (line == null || line.equals("") || line.startsWith("#")) {
		} else {
			final String[] values = line.split("=");
			checkKeyId(values[1]);
			final StrictPatternIncludesArtifactFilter filter = new StrictPatternIncludesArtifactFilter(Arrays.asList(values[0]));
			artifactKeyMap.put(filter, values[1].substring(2));
		}
	}

	public static void checkKeyId(final String keyId) {
		if (!keyIdPattern.matcher(keyId).matches()) {
			throw new RuntimeException("Bad keyId:" + keyId + " must match:" + keyIdPattern.pattern());
		}
	}

	@Override
	public String resolveKeyIdFor(final Artifact artifact) {
		final String keyId = artifactKeyMap.keySet().stream().filter(artifactPattern -> artifactPattern.include(artifact)).findAny()
				.map(artifactKeyMap::get).orElse(null);
		logger.debug("resolveKeyIdFor() artifact:" + artifact + " mapped to keyId:" + keyId);
		return keyId;
	}
}
