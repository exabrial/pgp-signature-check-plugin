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
import org.codehaus.plexus.logging.Logger;

import com.github.exabrial.checkpgpsignaturesplugin.interfaces.AscArtifactResolver;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.DependenciesLocator;
import com.github.exabrial.checkpgpsignaturesplugin.model.NoSignatureFileFoundException;

@Mojo(name = "pgp-signature-check", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = true)
public class PGPSignatureCheckMojo extends AbstractMojo {
	@Inject
	private ArtifactChecker artifactChecker;
	@Inject
	private AscArtifactResolver ascArtifactResolver;
	@Inject
	private DependenciesLocator dependenciesLocator;
	@Inject
	private Logger logger;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		logger.info("execute() checking artifact PGP signatures...");
		try {
			final Set<Artifact> filtered = dependenciesLocator.getArtifactsToVerify();
			filtered.forEach(artifact -> {
				final Artifact ascArtifact = ascArtifactResolver.resolveAscArtifact(artifact);
				if (ascArtifact == null) {
					throw new NoSignatureFileFoundException(artifact);
				}
				artifactChecker.check(artifact, ascArtifact);
			});
		} catch (final Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		logger.info("execute() checking artifact PGP signatures complete.");
	}
}
