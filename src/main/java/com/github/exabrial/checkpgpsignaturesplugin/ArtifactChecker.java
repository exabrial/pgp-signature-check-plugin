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

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.logging.Logger;

import com.github.exabrial.checkpgpsignaturesplugin.exceptions.MissingKeyMapException;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.KeyIdResolver;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.KeyRetriever;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.KeysCache;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.SignatureChecker;
import com.github.exabrial.checkpgpsignaturesplugin.model.PGPKey;

@Named
@Singleton
public class ArtifactChecker {
	@Inject
	private KeysCache pgpKeysCache;
	@Inject
	private KeyRetriever pgpKeyRetriever;
	@Inject
	private KeyIdResolver pgpKeyIdResolver;
	@Inject
	private SignatureChecker signatureChecker;
	@Inject
	private Logger logger;

	public void check(final Artifact artifact, final Artifact signature) {
		logger.debug("check() artifact:" + artifact);
		final String keyId = pgpKeyIdResolver.resolveKeyIdFor(artifact);
		if (keyId == null) {
			throw new MissingKeyMapException(artifact);
		} else {
			File keyRing = pgpKeysCache.getKeyLocation(keyId);
			if (keyRing == null) {
				final PGPKey pgpKey = pgpKeyRetriever.retrieveKey(keyId);
				keyRing = pgpKeysCache.put(pgpKey);
			}
			signatureChecker.checkArtifact(artifact.getFile(), signature.getFile(), keyRing, keyId);
		}
		logger.info("check() artifact:" + artifact + " signed with key:" + keyId);
	}
}
