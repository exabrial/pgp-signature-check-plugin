package com.github.exabrial.checkpgpsignaturesplugin.exceptions;

import org.apache.maven.artifact.Artifact;

public class MissingKeyMapException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MissingKeyMapException(final Artifact artifact) {
		super("There is no key mapped to:" + artifact);
	}
}
