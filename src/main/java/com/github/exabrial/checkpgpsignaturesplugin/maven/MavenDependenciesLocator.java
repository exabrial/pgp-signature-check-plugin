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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.ProjectDependenciesResolver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

import com.github.exabrial.checkpgpsignaturesplugin.interfaces.DependenciesLocator;

@Named
@Singleton
@SuppressWarnings("deprecation")
public class MavenDependenciesLocator implements DependenciesLocator {
	@Inject
	private MavenSession mavenSession;
	@Inject
	private MavenProject mavenProject;
	// TODO This is deprecated, need a replacement
	@Inject
	private ProjectDependenciesResolver projectDependenciesResolver;

	@Override
	public Set<Artifact> getArtifactsToVerify() {
		try {
			// TODO Make this a mojo option
			final List<String> scopes = Arrays.asList(new String[] { "compile", "runtime", "test" });
			final Set<Artifact> artifacts = projectDependenciesResolver.resolve(mavenProject, scopes, mavenSession);
			return artifacts;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
}
