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

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer;
import org.codehaus.plexus.util.cli.Commandline;

import com.github.exabrial.checkpgpsignaturesplugin.interfaces.SignatureChecker;
import com.github.exabrial.checkpgpsignaturesplugin.model.SignatureCheckFailedException;

@Named
@Singleton
public class GPGSignatureChecker implements SignatureChecker {
	private static final Pattern signingKeyPattern = Pattern.compile("^gpg:\\s+using (ECDSA|RSA|DSA) key ([a-z0-9]{16,40})$",
			Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
	private static final Pattern usingSubkeyPattern = Pattern.compile("^gpg:\\s+using subkey.*$",
			Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
	private static final Pattern subkeyPattern = Pattern.compile(
			"^gpg:\\s+using subkey ([a-z0-9]{16,40}) instead of primary key ([a-z0-9]{16,40})$",
			Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
	@Inject
	private GPGExecutable gpgExecutable;
	@Inject
	private Logger logger;

	@Override
	public void checkArtifact(final File artifactFile, final File signatureFile, final File keyRingFile, final String requiredKeyId)
			throws SignatureCheckFailedException {
		final Commandline cmd = new Commandline();
		cmd.setExecutable(gpgExecutable.getGPGExecutable());
		cmd.createArg().setValue("--verbose");
		cmd.createArg().setValue("--no-default-keyring");
		cmd.createArg().setValue("--keyring");
		cmd.createArg().setValue(keyRingFile.getAbsolutePath());
		cmd.createArg().setValue("--verify");
		cmd.createArg().setValue(signatureFile.getAbsolutePath());
		cmd.createArg().setValue(artifactFile.getAbsolutePath());
		logger.debug("checkArtifact() executing cmd:" + cmd);
		try {
			final StringStreamConsumer consumer = new StringStreamConsumer();
			final int exitCode = CommandLineUtils.executeCommandLine(cmd, consumer, consumer);
			if (exitCode != 0 || !isGoodOutput(consumer.getOutput(), requiredKeyId)) {
				throw new SignatureCheckFailedException(exitCode, consumer);
			}
		} catch (final CommandLineException e) {
			throw new RuntimeException(e);
		}
	}

	protected static boolean isGoodOutput(final String output, final String requiredKeyId) {
		try {
			final Matcher signingKeyMatcher = signingKeyPattern.matcher(output);
			if (signingKeyMatcher.find()) {
				final String longSubKey = signingKeyMatcher.group(2);
				if (usingSubkeyPattern.matcher(output).find()) {
					final Matcher subkeyKeyMatcher = subkeyPattern.matcher(output);
					subkeyKeyMatcher.find();
					final String shortSubKey = subkeyKeyMatcher.group(1);
					final String shortMasterKey = subkeyKeyMatcher.group(2);
					return requiredKeyId.endsWith(shortMasterKey) && longSubKey.endsWith(shortSubKey);
				} else {
					return requiredKeyId.endsWith(longSubKey);
				}
			} else {
				return false;
			}
		} catch (final Exception e) {
			return false;
		}
	}
}
