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

package com.github.exabrial.checkpgpsignaturesplugin.repo;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.DefaultRepositoryRequest;
import org.apache.maven.artifact.repository.RepositoryRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;

import com.github.exabrial.checkpgpsignaturesplugin.exceptions.NoASCArtifactException;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.AscArtifactResolver;

@Named
@Singleton
public class RepositoryAscArtifactResolver implements AscArtifactResolver {
	@Inject
	private RepositorySystem repositorySystem;
	@Inject
	private MavenSession session;
	@Inject
	private MavenProject project;

	@Override
	public Artifact resolveAscArtifact(final Artifact artifact) {
		final Artifact ascArtifact = repositorySystem.createArtifactWithClassifier(artifact.getGroupId(), artifact.getArtifactId(),
				artifact.getVersion(), artifact.getType(), artifact.getClassifier());
		final RepositoryRequest repositoryRequest = DefaultRepositoryRequest.getRepositoryRequest(session, project);
		final ArtifactResolutionRequest ascRequest = new ArtifactResolutionRequest(repositoryRequest);
		ascRequest.setResolveTransitively(false);
		ascRequest.setArtifact(ascArtifact);
		ascArtifact.setArtifactHandler(new AscArtifactHandler(ascArtifact));
		final ArtifactResolutionResult ascResult = repositorySystem.resolve(ascRequest);
		if (ascResult.isSuccess()) {
			final Artifact ascResultArtifact = ascResult.getArtifacts().stream().findFirst().get();
			return ascResultArtifact;
		} else {
			throw new NoASCArtifactException(artifact);
		}
	}
}
