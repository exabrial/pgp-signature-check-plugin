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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MojoPropertiesTest {
	@InjectMocks
	private MojoProperties mojoProperties;
	@Mock
	private MavenSession mavenSession;
	@Mock
	private MojoExecution execution;
	@Mock
	private MavenProject mavenProject;
	@Mock
	private Xpp3Dom xpp3Dom;
	final Properties props = new Properties();
	@Mock
	private Xpp3Dom child;

	@BeforeEach
	public void beforeEach() {
		props.clear();
		when(mavenSession.getCurrentProject()).thenReturn(mavenProject);
		when(mavenSession.getUserProperties()).thenReturn(props);
		when(mavenSession.getSystemProperties()).thenReturn(props);
		when(execution.getConfiguration()).thenReturn(xpp3Dom);
	}

	@Test
	void testGetProperty_null() {
		mojoProperties.postConstruct();
		final String value = mojoProperties.getProperty("gpgExecutable");
		assertEquals(null, value);
	}

	@Test
	void testGetProperty_child() {
		when(xpp3Dom.getChild(anyString())).thenReturn(child);
		final String itWorks = "it works";
		when(child.getValue()).thenReturn(itWorks);
		mojoProperties.postConstruct();
		final String value = mojoProperties.getProperty("keyMapFileName");
		assertEquals(itWorks, value);
	}

	@Test
	void testGetProperty_defaultValueNull() {
		when(xpp3Dom.getChild(anyString())).thenReturn(child);
		mojoProperties.postConstruct();
		final String value = mojoProperties.getProperty("keyMapFileName");
		assertEquals(null, value);
	}

	@Test
	void testGetProperty_defaultValue() {
		when(xpp3Dom.getChild(anyString())).thenReturn(child);
		final String itsDefault = "itsDefault";
		when(child.getAttribute(anyString())).thenReturn(itsDefault);
		mojoProperties.postConstruct();
		final String value = mojoProperties.getProperty("keyMapFileName");
		assertEquals(itsDefault, value);
	}
}
