package com.github.exabrial.checkpgpsignaturesplugin;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.github.exabrial.checkpgpsignaturesplugin.exceptions.MissingKeyMapException;

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
	public void testCheck() {
		when(pgpKeyIdResolver.resolveKeyIdFor(artifact)).thenReturn(keyId);

		try {
			artifactChecker.check(artifact, signature);
		} catch (final Exception e) {
			e.printStackTrace();
			fail("failed with exception");
		}
	}

	@Test(expected = MissingKeyMapException.class)
	public void testCheck_noMappedKey() {
		artifactChecker.check(artifact, signature);
	}

	@Test
	public void testCheck_keyLocationNotNull() {
		when(pgpKeyIdResolver.resolveKeyIdFor(artifact)).thenReturn(keyId);
		when(pgpKeysCache.getKeyLocation(keyId)).thenReturn(mock(File.class));

		try {
			artifactChecker.check(artifact, signature);
		} catch (final Exception e) {
			e.printStackTrace();
			fail("failed with exception");
		}
	}

}
