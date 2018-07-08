package com.github.exabrial.checkpgpsignaturesplugin;

import java.util.Arrays;
import java.util.Set;

import javax.inject.Inject;

import org.apache.maven.ProjectDependenciesResolver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.DefaultRepositoryRequest;
import org.apache.maven.artifact.repository.RepositoryRequest;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.logging.Logger;

@SuppressWarnings("deprecation")
@Mojo(name = "check-signatures", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresProject = true)
public class CheckPGPSignaturesMojo extends AbstractMojo {
	@Inject
	private RepositorySystem repositorySystem;
	@Inject
	private MavenSession session;
	@Inject
	private MavenProject project;
	@Inject
	private ArtifactChecker artifactChecker;
	// TODO This is deprecated, need a replacement
	@Inject
	private ProjectDependenciesResolver projectDependenciesResolver;
	@Inject
	private Logger logger;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		logger.info("execute() checking artifact PGP signatures...");
		try {
			final Set<Artifact> filtered = getArtifactsToVerify();
			filtered.forEach(artifact -> artifactChecker.check(artifact, resolveAscArtifact(artifact)));
		} catch (final Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
		logger.info("execute() checking artifact PGP signatures complete.");
	}

	Set<Artifact> getArtifactsToVerify() {
		Set<Artifact> artifacts;
		try {
			artifacts = projectDependenciesResolver.resolve(project, Arrays.asList(new String[] { "compile", "runtime", "test" }), session);
		} catch (ArtifactResolutionException | ArtifactNotFoundException e) {
			throw new RuntimeException(e);
		}
		return artifacts;
	}

	Artifact resolveAscArtifact(final Artifact artifact) {
		final Artifact ascArtifact = repositorySystem.createArtifactWithClassifier(artifact.getGroupId(), artifact.getArtifactId(),
				artifact.getVersion(), artifact.getType(), artifact.getClassifier());
		final RepositoryRequest repositoryRequest = DefaultRepositoryRequest.getRepositoryRequest(session, project);
		final ArtifactResolutionRequest ascRequest = new ArtifactResolutionRequest(repositoryRequest);
		ascRequest.setResolveTransitively(false);
		ascRequest.setArtifact(ascArtifact);
		ascArtifact.setArtifactHandler(new AscArtifactHandler(ascArtifact));
		final ArtifactResolutionResult ascResult = repositorySystem.resolve(ascRequest);
		if (ascResult.isSuccess()) {
			final Artifact ascResultArtifact = ascResult.getArtifacts().stream().findFirst().orElse(null);
			return ascResultArtifact;
		} else {
			throw new RuntimeException("Could not resolve signature artifact:" + artifact);
		}
	}
}
