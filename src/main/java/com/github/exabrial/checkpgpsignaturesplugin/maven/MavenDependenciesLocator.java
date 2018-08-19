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

package com.github.exabrial.checkpgpsignaturesplugin.maven;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;

import com.github.exabrial.checkpgpsignaturesplugin.MojoProperties;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.DependenciesLocator;
import com.github.exabrial.checkpgpsignaturesplugin.model.NoProjectArtifactFoundException;

@Named
@Singleton
public class MavenDependenciesLocator implements DependenciesLocator {
	@Inject
	private MavenProject mavenProject;
	@Inject
	private RepositorySystem repositorySystem;
	@Parameter(defaultValue = "${localRepository}", readonly = true)
	private ArtifactRepository localRepository;
	@Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true)
	private List<ArtifactRepository> remoteRepositories;
	@Inject
	private MojoProperties mojoProperties;

	@Override
	public Set<Artifact> getArtifactsToVerify() {
		final Set<Artifact> artifacts = mavenProject.getArtifacts();
		if ("true".equalsIgnoreCase(mojoProperties.getProperty("checkPomSignatures"))) {
			artifacts.addAll(getPomArtifacts(artifacts));
		}
		return artifacts;
	}

	private Set<Artifact> getPomArtifacts(final Set<Artifact> artifacts) {
		final Set<Artifact> poms = new HashSet<>();
		for (final Artifact artifact : artifacts) {
			final ArtifactResolutionRequest artifactResolutionRequest = createArtifactResolutionRequestForPom(artifact);
			final ArtifactResolutionResult artifactResolutionResult = repositorySystem.resolve(artifactResolutionRequest);
			poms.add(
					artifactResolutionResult.getArtifacts().stream().findAny().orElseThrow(() -> new NoProjectArtifactFoundException(artifact)));
		}
		return poms;
	}

	private ArtifactResolutionRequest createArtifactResolutionRequestForPom(final Artifact artifact) {
		final Artifact projectArtifact = repositorySystem.createProjectArtifact(artifact.getGroupId(), artifact.getArtifactId(),
				artifact.getVersion());
		final ArtifactResolutionRequest artifactResolutionRequest = new ArtifactResolutionRequest();
		artifactResolutionRequest.setArtifact(projectArtifact);
		artifactResolutionRequest.setResolveTransitively(false);
		artifactResolutionRequest.setLocalRepository(localRepository);
		artifactResolutionRequest.setRemoteRepositories(remoteRepositories);
		return artifactResolutionRequest;
	}
}