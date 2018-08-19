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

package com.github.exabrial.checkpgpsignaturesplugin.gpg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.cli.Commandline;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.exabrial.checkpgpsignaturesplugin.model.CouldntRetrieveKeyException;
import com.github.exabrial.checkpgpsignaturesplugin.model.PGPKey;

@ExtendWith(MockitoExtension.class)
public class GPGKeyRetrieverTest {
	@InjectMocks
	private TestGPGKeyRetriever gpgKeyRetriever;
	@Mock
	private CommandExecutor commandExecutor;
	@Mock
	private GPGLocator gpgExecutable;
	@Mock
	private Logger logger;
	private final String keyId = "871638A21A7F2C38066471420306A354336B4F0D";
	private static final byte[] keyBytes = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };

	@Test
	public void testRetrieveKey() throws Exception {
		final ExecutionResult result = new ExecutionResult(0, "got said key");
		when(commandExecutor.execute(any(Commandline.class))).thenReturn(result);
		final PGPKey pgpKey = gpgKeyRetriever.retrieveKey(keyId);
		assertEquals(keyId, pgpKey.keyId);
		assertTrue(Arrays.equals(keyBytes, pgpKey.keyData));
	}

	@Test
	public void testRetrieveKey_gpgNonZeroExitCode() throws Exception {
		final Executable executable = () -> {
			final ExecutionResult result = new ExecutionResult(3, "didnt get key");
			when(commandExecutor.execute(any(Commandline.class))).thenReturn(result);
			gpgKeyRetriever.retrieveKey(keyId);
		};
		assertThrows(CouldntRetrieveKeyException.class, executable);
	}

	@Test
	public void testRetrieveKey_ioe() throws Exception {
		final Executable executable = () -> {
			gpgKeyRetriever.throwIoe = true;
			gpgKeyRetriever.retrieveKey(keyId);
		};
		assertThrows(CouldntRetrieveKeyException.class, executable);
	}

	/**
	 * Not proud of this. Need a better way.
	 */
	private static class TestGPGKeyRetriever extends GPGKeyRetriever {
		private File file;
		private boolean throwIoe = false;

		@Override
		public File createTempFile(final String keyId) throws IOException {
			if (throwIoe) {
				throw new IOException();
			} else {
				file = super.createTempFile(keyId);
				FileUtils.writeByteArrayToFile(file, keyBytes);
				return file;
			}
		}
	}
}
