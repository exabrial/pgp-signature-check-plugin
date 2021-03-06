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
import javax.inject.Singleton;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.cli.Commandline;

import com.github.exabrial.checkpgpsignaturesplugin.MojoProperties;
import com.github.exabrial.checkpgpsignaturesplugin.model.CantFindGPGException;

@Named
@Singleton
public class GPGLocator {
	@Inject
	private Logger logger;
	@Inject
	private CommandExecutor commandExecutor;
	@Inject
	private MojoProperties mojoProperties;
	private String gpgExecutable;

	@PostConstruct
	public void postConstruct() {
		final Commandline cmd = new Commandline();
		if ((gpgExecutable = mojoProperties.getProperty("gpgExecutable")) == null) {
			if (isWindows()) {
				cmd.setExecutable("where.exe");
				cmd.createArg().setValue("gpg.exe");
			} else {
				cmd.setExecutable("which");
				cmd.createArg().setValue("gpg");
			}
			final ExecutionResult result = commandExecutor.execute(cmd);
			if (result.exitCode != 0) {
				throw new CantFindGPGException(result);
			} else {
				gpgExecutable = result.output.trim();
			}
		}
		logger.info("postConstruct() using gpgExecutable:" + gpgExecutable);
	}

	protected boolean isWindows() {
		// :( Static methods are the death of testing
		return Os.isFamily(Os.FAMILY_WINDOWS);
	}

	public String getGPGExecutable() {
		return gpgExecutable.toString();
	}
}
