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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.cli.Commandline;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.exabrial.checkpgpsignaturesplugin.model.CouldntRetrieveKeyException;
import com.github.exabrial.checkpgpsignaturesplugin.model.PGPKey;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FileUtils.class)
public class GPGKeyRetrieverTest {
	@InjectMocks
	private GPGKeyRetriever gpgKeyRetriever;
	@Mock
	private CommandExecutor commandExecutor;
	@Mock
	private GPGExecutable gpgExecutable;
	@Mock
	private Logger logger;
	private final String keyId = "871638A21A7F2C38066471420306A354336B4F0D";

	@Test
	public void testRetrieveKey() throws Exception {
		final ExecutionResult result = new ExecutionResult(0, "got said key");
		when(commandExecutor.execute(any(Commandline.class))).thenReturn(result);
		final byte[] keyBytes = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 };
		PowerMockito.mockStatic(FileUtils.class);
		PowerMockito.when(FileUtils.readFileToByteArray(any(File.class))).thenReturn(keyBytes);
		final PGPKey pgpKey = gpgKeyRetriever.retrieveKey(keyId);
		assertEquals(keyId, pgpKey.keyId);
		assertEquals(keyBytes, pgpKey.keyData);
	}

	@Test(expected = CouldntRetrieveKeyException.class)
	public void testRetrieveKey_gpgNonZeroExitCode() throws Exception {
		final ExecutionResult result = new ExecutionResult(3, "didnt get key");
		when(commandExecutor.execute(any(Commandline.class))).thenReturn(result);
		gpgKeyRetriever.retrieveKey(keyId);
	}

	@Test(expected = CouldntRetrieveKeyException.class)
	public void testRetrieveKey_ioe() throws Exception {
		final ExecutionResult result = new ExecutionResult(0, "got said key");
		when(commandExecutor.execute(any(Commandline.class))).thenReturn(result);
		PowerMockito.mockStatic(FileUtils.class);
		PowerMockito.when(FileUtils.readFileToByteArray(any(File.class))).thenThrow(new IOException());
		gpgKeyRetriever.retrieveKey(keyId);
	}
}
