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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.apache.maven.artifact.handler.ArtifactHandler;
import org.junit.jupiter.api.Test;

public class AscArtifactHandlerTest {
	@Test
	public void testGetExtension() {
		final ArtifactHandler wrappedHandler = mock(ArtifactHandler.class);
		when(wrappedHandler.getExtension()).thenReturn("");
		final String extension = new AscArtifactHandler(wrappedHandler).getExtension();
		assertEquals(".asc", extension);
		verify(wrappedHandler).getExtension();
		verifyNoMoreInteractions(wrappedHandler);
	}

	@Test
	public void testGetDirectory() {
		final ArtifactHandler wrappedHandler = mock(ArtifactHandler.class);
		new AscArtifactHandler(wrappedHandler).getDirectory();
		verify(wrappedHandler).getDirectory();
		verifyNoMoreInteractions(wrappedHandler);
	}

	@Test
	public void testGetClassifier() {
		final ArtifactHandler wrappedHandler = mock(ArtifactHandler.class);
		new AscArtifactHandler(wrappedHandler).getClassifier();
		verify(wrappedHandler).getClassifier();
		verifyNoMoreInteractions(wrappedHandler);
	}

	@Test
	public void testGetPackaging() {
		final ArtifactHandler wrappedHandler = mock(ArtifactHandler.class);
		new AscArtifactHandler(wrappedHandler).getPackaging();
		verify(wrappedHandler).getPackaging();
		verifyNoMoreInteractions(wrappedHandler);
	}

	@Test
	public void testIsIncludesDependencies() {
		final ArtifactHandler wrappedHandler = mock(ArtifactHandler.class);
		new AscArtifactHandler(wrappedHandler).isIncludesDependencies();
		verify(wrappedHandler).isIncludesDependencies();
		verifyNoMoreInteractions(wrappedHandler);
	}

	@Test
	public void testGetLanguage() {
		final ArtifactHandler wrappedHandler = mock(ArtifactHandler.class);
		new AscArtifactHandler(wrappedHandler).getLanguage();
		verify(wrappedHandler).getLanguage();
		verifyNoMoreInteractions(wrappedHandler);
	}

	@Test
	public void testIsAddedToClasspath() {
		final ArtifactHandler wrappedHandler = mock(ArtifactHandler.class);
		new AscArtifactHandler(wrappedHandler).isAddedToClasspath();
		verify(wrappedHandler).isAddedToClasspath();
		verifyNoMoreInteractions(wrappedHandler);
	}
}
