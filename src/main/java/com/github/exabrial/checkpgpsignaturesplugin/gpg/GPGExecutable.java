package com.github.exabrial.checkpgpsignaturesplugin.gpg;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
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
	private String gpgExecutable;

	@PostConstruct
	public void postConstruct() {
		final Commandline cmd = new Commandline();
		if (gpgExecutable == null) {
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
