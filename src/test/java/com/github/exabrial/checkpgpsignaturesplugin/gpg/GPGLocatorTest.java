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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.cli.Commandline;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.exabrial.checkpgpsignaturesplugin.MojoProperties;
import com.github.exabrial.checkpgpsignaturesplugin.model.CantFindGPGException;

@ExtendWith(MockitoExtension.class)
public class GPGLocatorTest {
	@InjectMocks
	private GPGLocator gpgLocator;
	@Mock
	private MojoProperties mojoProperties;
	@Mock
	private CommandExecutor commandExecutor;
	@Mock
	private Logger logger;

	@Test
	public void testPostConstruct_unix() {
		final String whichCommandOutput = "woot2";
		final ExecutionResult result = new ExecutionResult(0, whichCommandOutput);
		when(commandExecutor.execute(any(Commandline.class))).thenReturn(result);
		gpgLocator.postConstruct();
		assertEquals(whichCommandOutput, gpgLocator.getGPGExecutable());
	}

	@Test
	public void testPostConstruct_windows() {
		final GPGLocator gpgLocator = spy(this.gpgLocator);
		when(gpgLocator.isWindows()).thenReturn(Boolean.TRUE);
		final String whichCommandOutput = "woot3";
		final ExecutionResult result = new ExecutionResult(0, whichCommandOutput);
		when(commandExecutor.execute(any(Commandline.class))).thenReturn(result);
		gpgLocator.postConstruct();
		assertEquals(whichCommandOutput, gpgLocator.getGPGExecutable());
	}

	@Test
	public void testPostConstruct_provideGpgExecutable() {
		final String gpgExecutable = "YEE YEE - Earl Dibbles Jr";
		when(mojoProperties.getProperty("gpgExecutable")).thenReturn(gpgExecutable);
		gpgLocator.postConstruct();
		verifyZeroInteractions(commandExecutor);
		assertEquals(gpgExecutable, gpgLocator.getGPGExecutable());
	}

	@Test
	public void testPostConstruct_missingGpg() {
		final Executable executable = () -> {
			final String whichCommandOutput = "";
			final ExecutionResult result = new ExecutionResult(1, whichCommandOutput);
			when(commandExecutor.execute(any(Commandline.class))).thenReturn(result);
			gpgLocator.postConstruct();
		};
		assertThrows(CantFindGPGException.class, executable);
	}
}
