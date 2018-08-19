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

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * Maven dependency injection is a joke. It's impossible to inject a parameter
 * into a sub-component. So here we are, doing terrible things to perfectly good
 * JVMs who don't deserve it.
 */
@Named
@Singleton
public class MojoProperties {
	@Inject
	private MavenSession session;
	@Inject
	private MojoExecution execution;
	private final Properties properties = new Properties();
	private PluginParameterExpressionEvaluator evaluator;

	public String getProperty(final String key) {
		return properties.getProperty(key);
	}

	@PostConstruct
	protected void postConstruct() {
		evaluator = new PluginParameterExpressionEvaluator(session, execution);
		setIfNotNull("gpgExecutable");
		setIfNotNull("keyCacheDirectory");
		setIfNotNull("keyMapFileName");
		setIfNotNull("checkPomSignatures");
	}

	void setIfNotNull(final String parameterName) {
		final String parameter = getParameter(parameterName);
		if (parameter != null) {
			properties.setProperty(parameterName, parameter);
		}
	}

	private String getParameter(final String parameterName) {
		final Xpp3Dom configuration = execution.getConfiguration();
		final Xpp3Dom child = configuration.getChild(parameterName);
		if (child != null) {
			String value = child.getValue();
			if (value == null) {
				value = child.getAttribute("default-value");
			}
			if (value != null) {
				try {
					value = (String) evaluator.evaluate(value);
				} catch (final ExpressionEvaluationException e) {
					throw new RuntimeException(e);
				}
			}
			return value;
		} else {
			return null;
		}
	}
}
