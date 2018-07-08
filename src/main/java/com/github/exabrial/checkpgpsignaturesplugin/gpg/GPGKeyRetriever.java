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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer;
import org.codehaus.plexus.util.cli.Commandline;

import com.github.exabrial.checkpgpsignaturesplugin.interfaces.KeyRetriever;
import com.github.exabrial.checkpgpsignaturesplugin.model.PGPKey;

@Named
@Singleton
public class GPGKeyRetriever implements KeyRetriever {
	@Inject
	private GPGExecutable gpgExecutable;
	@Inject
	private Logger logger;

	@Override
	public PGPKey retrieveKey(final String keyId) {
		logger.info("retrieveKey() fetching keyId:" + keyId);
		try {
			final File tempKeyFile = File.createTempFile(keyId, "kbx");
			final Commandline cmd = new Commandline();
			cmd.setExecutable(gpgExecutable.getGPGExecutable());
			cmd.createArg().setValue("--verbose");
			cmd.createArg().setValue("--recv-keys");
			cmd.createArg().setValue("--no-default-keyring");
			cmd.createArg().setValue("--keyserver");
			cmd.createArg().setValue("pool.sks-keyservers.net");
			cmd.createArg().setValue("--keyring");
			cmd.createArg().setValue(tempKeyFile.getAbsolutePath());
			cmd.createArg().setValue(keyId);
			logger.debug("retrieveKey() executing cmd:" + cmd);
			final StringStreamConsumer consumer = new StringStreamConsumer();
			final int exitCode = CommandLineUtils.executeCommandLine(cmd, consumer, consumer);
			if (exitCode != 0) {
				logger.error("retrieveKey() consumer.getOutput():" + consumer.getOutput());
				throw new RuntimeException("Could not retrieve key:" + keyId + ", gpg program gave exit code:" + exitCode);
			} else {
				try (FileInputStream fis = new FileInputStream(tempKeyFile)) {
					try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
						final byte[] buffer = new byte[1024];
						int byteRead;
						while ((byteRead = fis.read(buffer)) != -1) {
							baos.write(buffer, 0, byteRead);
						}
						return new PGPKey(keyId, baos.toByteArray());
					}
				} finally {
					tempKeyFile.delete();
				}
			}
		} catch (final CommandLineException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}
