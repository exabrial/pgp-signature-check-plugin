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

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.cli.Commandline;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommandExecutorTest {
	@InjectMocks
	private CommandExecutor commandExecutor;
	@Mock
	private Logger logger;

	@Test
	public void testExecute() {
		final Commandline cmd = new Commandline();
		if (Os.isFamily(Os.FAMILY_WINDOWS)) {
			cmd.setExecutable("where.exe");
			cmd.createArg().setValue("where.exe");
		} else {
			cmd.setExecutable("which");
			cmd.createArg().setValue("which");
		}
		final ExecutionResult result = commandExecutor.execute(cmd);
		assertEquals(0, result.exitCode);
	}

	@Test
	public void testExecute_err() {
		final Executable executable = () -> {
			final Commandline cmd = new Commandline();
			cmd.setExecutable("/AsDFDoestExist123458120");
			commandExecutor.execute(cmd);
		};
		assertThrows(RuntimeException.class, executable);
	}
}
