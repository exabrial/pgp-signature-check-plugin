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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer;
import org.codehaus.plexus.util.cli.Commandline;
import org.eclipse.sisu.Nullable;

@Named
@Singleton
public class GPGExecutable {
	@Inject
	private Logger logger;
	@Inject
	@Nullable
	@Named("${gpgExecutable}")
	private Provider<String> gpgExecutableProvider;
	private String gpgExecutable;

	@PostConstruct
	public void postConstruct() {
		final Commandline cmd = new Commandline();
		if ((gpgExecutable = gpgExecutableProvider.get()) == null) {
			try {
				if (Os.isFamily(Os.FAMILY_WINDOWS)) {
					cmd.setExecutable("where.exe");
					cmd.createArg().setValue("gpg.exe");
				} else {
					cmd.setExecutable("which");
					cmd.createArg().setValue("gpg");
				}
				final StringStreamConsumer consumer = new StringStreamConsumer();
				final int exitCode = CommandLineUtils.executeCommandLine(cmd, consumer, consumer);
				if (exitCode != 0) {
					logger.error("postConstruct() consumer.getOutput():" + consumer.getOutput());
					throw new RuntimeException("Could not find gpg executable");
				} else {
					gpgExecutable = consumer.getOutput().trim();
				}
			} catch (final CommandLineException e) {
				throw new RuntimeException(e);
			}
		}
		logger.info("postConstruct() using gpgExecutable:" + gpgExecutable);
	}

	public String getGPGExecutable() {
		return gpgExecutable;
	}
}
