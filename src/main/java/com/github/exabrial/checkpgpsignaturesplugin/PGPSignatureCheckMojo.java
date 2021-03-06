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

import java.util.Set;

import javax.inject.Inject;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.logging.Logger;

import com.github.exabrial.checkpgpsignaturesplugin.interfaces.AscArtifactResolver;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.DependenciesLocator;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.KeyIdResolver;
import com.github.exabrial.checkpgpsignaturesplugin.model.MissingKeyMappingException;
import com.github.exabrial.checkpgpsignaturesplugin.model.NoSignatureFileFoundException;
import com.github.exabrial.checkpgpsignaturesplugin.model.SignatureCheckFailedException;

/**
 * This goal will download the asc signatures for artifacts in the build and
 * verify their integrity.
 *
 * @since 1.0.0
 */
@Mojo(name = "pgp-signature-check",
			defaultPhase = LifecyclePhase.PROCESS_RESOURCES,
			requiresProject = true,
			requiresDependencyResolution = ResolutionScope.TEST)
public class PGPSignatureCheckMojo extends AbstractMojo {
	@Inject
	private ArtifactChecker artifactChecker;
	@Inject
	private AscArtifactResolver ascArtifactResolver;
	@Inject
	private DependenciesLocator dependenciesLocator;
	@Inject
	private KeyIdResolver pgpKeyIdResolver;
	@Inject
	private Logger logger;
	/**
	 * The fully qualified path to the gpg executable. If not specified, the plugin
	 * will perform a which/where.exe lookup
	 */
	@Parameter
	private String gpgExecutable;
	/**
	 * The fully qualified path to the directory where pgp keys will be cached. The
	 * plugin will not automatically create this directory if it doesn't exist.
	 */
	@Parameter(defaultValue = "~/.m2/artifactPubKeys")
	private String keyCacheDirectory;
	/**
	 * The fully qualified path of the file used to pin artifacts to pgp keys. The
	 * format is:
	 * <p>
	 * groupId:artifactId:version=0xPGPKeyFingerprint (16-40 hex chars)
	 * </p>
	 * So for bouncycastle, that'd look like:
	 * <p>
	 * org.bouncycastle:*:*=0x08F0AAB4D0C1A4BDDE340765B341DDB020FCB6AB
	 * </p>
	 * Wildcards are allowed. All lines are trimmed. Comments begin the # [hash]
	 * character.
	 * <p>
	 * To skip a particular artifact, set the value to: `skip-signature-check`
	 * </p>
	 * groupId:artifactId:version=skip-signature-check
	 */
	@Parameter(defaultValue = "${project.basedir}/artifact-key-map.txt")
	private String keyMapFileName;
	/**
	 * Indicates whether pom file (project artifact) signatures should be checked as
	 * well.
	 */
	@Parameter(defaultValue = "true")
	private boolean checkPomSignatures;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		logger.info("execute() checking artifact PGP signatures...");
		try {
			final Set<Artifact> filtered = dependenciesLocator.getArtifactsToVerify();
			filtered.forEach(artifact -> {
				if (!pgpKeyIdResolver.isVerificationSkipped(artifact)) {
					final String keyId = pgpKeyIdResolver.resolveKeyIdFor(artifact);
					if (keyId == null) {
						throw new MissingKeyMappingException(artifact);
					}
					final Artifact ascArtifact = ascArtifactResolver.resolveAscArtifact(artifact);
					if (ascArtifact == null) {
						throw new NoSignatureFileFoundException(artifact);
					}
					artifactChecker.check(artifact, ascArtifact, keyId);
				} else {
					logger.warn("execute() per user request, skipping signature check on artifact:" + artifact);
				}
			});
		} catch (final SignatureCheckFailedException e) {
			throw new MojoFailureException(e.getMessage(), e);
		} catch (final Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		logger.info("execute() checking artifact PGP signatures complete.");
	}

}
