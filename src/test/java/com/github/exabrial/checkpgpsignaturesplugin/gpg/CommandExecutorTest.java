package com.github.exabrial.checkpgpsignaturesplugin.gpg;

import static org.junit.Assert.assertEquals;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.cli.Commandline;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
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

	@Test(expected = RuntimeException.class)
	public void testExecute_err() {
		final Commandline cmd = new Commandline();
		cmd.setExecutable("/AsDFDoestExist123458120");
		commandExecutor.execute(cmd);
	}
}
