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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.logging.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.exabrial.checkpgpsignaturesplugin.interfaces.KeyRetriever;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.KeysCache;
import com.github.exabrial.checkpgpsignaturesplugin.interfaces.SignatureChecker;
import com.github.exabrial.checkpgpsignaturesplugin.model.PGPKey;

@ExtendWith(MockitoExtension.class)
public class ArtifactCheckerTest {
	@InjectMocks
	private ArtifactChecker artifactChecker;
	@Mock
	private KeysCache pgpKeysCache;
	@Mock
	private KeyRetriever pgpKeyRetriever;
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
		final PGPKey pgpKey = mock(PGPKey.class);
		when(pgpKeyRetriever.retrieveKey(keyId)).thenReturn(pgpKey);
		artifactChecker.check(artifact, signature, keyId);
		verify(pgpKeysCache).getKeyFile(keyId);
		verify(pgpKeyRetriever).retrieveKey(keyId);
		verify(pgpKeysCache).put(pgpKey);
	}

	@Test
	public void testCheck_cacheHit() throws Exception {
		when(pgpKeysCache.getKeyFile(keyId)).thenReturn(mock(File.class));
		artifactChecker.check(artifact, signature, keyId);
		verify(pgpKeysCache).getKeyFile(keyId);
		verify(pgpKeysCache, never()).put(any(PGPKey.class));
		verifyZeroInteractions(pgpKeyRetriever);
	}

	@Test
	public void testCheck_ioeOnPut() throws Exception {
		final Executable executable = () -> {
			final PGPKey pgpKey = mock(PGPKey.class);
			when(pgpKeyRetriever.retrieveKey(keyId)).thenReturn(pgpKey);
			when(pgpKeysCache.put(pgpKey)).thenThrow(new IOException());
			artifactChecker.check(artifact, signature, keyId);
		};
		assertThrows(RuntimeException.class, executable);
	}
}
