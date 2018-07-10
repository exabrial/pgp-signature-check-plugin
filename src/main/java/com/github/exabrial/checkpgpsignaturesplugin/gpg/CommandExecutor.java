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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer;
import org.codehaus.plexus.util.cli.Commandline;

@Named
@Singleton
public class CommandExecutor {
	@Inject
	private Logger logger;

	public ExecutionResult execute(final Commandline commandLine) {
		logger.debug("execute() executing commandLine:" + commandLine);
		final StringStreamConsumer consumer = new StringStreamConsumer();
		try {
			final int exitCode = CommandLineUtils.executeCommandLine(commandLine, consumer, consumer);
			return new ExecutionResult(exitCode, consumer.getOutput().trim());
		} catch (final CommandLineException e) {
			throw new RuntimeException(e);
		}
	}
}
