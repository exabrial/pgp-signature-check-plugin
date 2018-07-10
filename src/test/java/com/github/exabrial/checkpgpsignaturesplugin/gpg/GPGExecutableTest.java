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
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.inject.Provider;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.cli.Commandline;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.exabrial.checkpgpsignaturesplugin.model.CantFindGPGException;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Os.class)
public class GPGExecutableTest {
	@InjectMocks
	private GPGExecutable gpgExecutable;
	@Mock
	private CommandExecutor commandExecutor;
	@Mock
	private Logger logger;
	@Mock
	private Provider<String> gpgExecutableProvider;

	@Test
	public void testPostConstruct_unix() {
		final String whichCommandOutput = "woot2";
		final ExecutionResult result = new ExecutionResult(0, whichCommandOutput);
		PowerMockito.spy(Os.class);
		when(commandExecutor.execute(any(Commandline.class))).thenReturn(result);
		PowerMockito.when(Os.isFamily(Os.FAMILY_WINDOWS)).thenReturn(false);
		gpgExecutable.postConstruct();
		assertEquals(whichCommandOutput, gpgExecutable.getGPGExecutable());
	}

	@Test
	public void testPostConstruct_windows() {
		final String whichCommandOutput = "woot3";
		final ExecutionResult result = new ExecutionResult(0, whichCommandOutput);
		PowerMockito.spy(Os.class);
		when(commandExecutor.execute(any(Commandline.class))).thenReturn(result);
		PowerMockito.when(Os.isFamily(Os.FAMILY_WINDOWS)).thenReturn(true);
		gpgExecutable.postConstruct();
		assertEquals(whichCommandOutput, gpgExecutable.getGPGExecutable());
	}

	@Test
	public void testPostConstruct_provideGpgExecutable() {
		final String gpgExectuable = "YEE YEE - Earl Dibbles Jr";
		when(gpgExecutableProvider.get()).thenReturn(gpgExectuable);
		gpgExecutable.postConstruct();
		verifyZeroInteractions(commandExecutor);
		assertEquals(gpgExectuable, gpgExecutable.getGPGExecutable());
	}

	@Test(expected = CantFindGPGException.class)
	public void testPostConstruct_missingGpg() {
		final String whichCommandOutput = "";
		final ExecutionResult result = new ExecutionResult(1, whichCommandOutput);
		when(commandExecutor.execute(any(Commandline.class))).thenReturn(result);
		gpgExecutable.postConstruct();
	}
}
