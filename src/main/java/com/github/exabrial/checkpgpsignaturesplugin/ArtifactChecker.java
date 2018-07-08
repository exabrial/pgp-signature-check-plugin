package com.github.exabrial.checkpgpsignaturesplugin;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.logging.Logger;

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
			throw new RuntimeException("There is no key mapped to:" + artifact);
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
