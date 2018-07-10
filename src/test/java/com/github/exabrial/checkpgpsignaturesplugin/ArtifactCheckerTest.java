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

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.exabrial.checkpgpsignaturesplugin.interfaces.KeyIdResolver;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.KeyRetriever;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.KeysCache;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.SignatureChecker;
import com.github.exabrial.checkpgpsignaturesplugin.model.MissingKeyMappingException;
import com.github.exabrial.checkpgpsignaturesplugin.model.PGPKey;

@RunWith(MockitoJUnitRunner.class)
public class ArtifactCheckerTest {
	@InjectMocks
	private ArtifactChecker artifactChecker;
	@Mock
	private KeysCache pgpKeysCache;
	@Mock
	private KeyRetriever pgpKeyRetriever;
	@Mock
	private KeyIdResolver pgpKeyIdResolver;
	@Mock
	private SignatureChecker signatureChecker;
	@Mock
	private Logger logger;
	@Mock
	private Artifact artifact;
	@Mock
	private Artifact signature;
	private final String keyId = "kid0";

	@Test
	public void testCheck_cacheMiss() throws Exception {
		when(pgpKeyIdResolver.resolveKeyIdFor(artifact)).thenReturn(keyId);
		final PGPKey pgpKey = mock(PGPKey.class);
		when(pgpKeyRetriever.retrieveKey(keyId)).thenReturn(pgpKey);

		try {
			artifactChecker.check(artifact, signature);
		} catch (final Exception e) {
			e.printStackTrace();
			fail("failed with exception");
		}

		verify(pgpKeysCache).getKeyFile(keyId);
		verify(pgpKeyRetriever).retrieveKey(keyId);
		verify(pgpKeysCache).put(pgpKey);
	}

	@Test
	public void testCheck_cacheHit() throws Exception {
		when(pgpKeyIdResolver.resolveKeyIdFor(artifact)).thenReturn(keyId);
		when(pgpKeysCache.getKeyFile(keyId)).thenReturn(mock(File.class));

		try {
			artifactChecker.check(artifact, signature);
		} catch (final Exception e) {
			e.printStackTrace();
			fail("failed with exception");
		}
		verify(pgpKeysCache).getKeyFile(keyId);
		verify(pgpKeysCache, never()).put(any(PGPKey.class));
		verifyZeroInteractions(pgpKeyRetriever);
	}

	@Test(expected = MissingKeyMappingException.class)
	public void testCheck_noMappedKey() {
		artifactChecker.check(artifact, signature);
	}
}
