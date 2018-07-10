package com.github.exabrial.checkpgpsignaturesplugin.model;

import com.github.exabrial.checkpgpsignaturesplugin.gpg.ExecutionResult;

public class CouldntRetrieveKeyException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CouldntRetrieveKeyException(final ExecutionResult result, final String keyId) {
		super("Could not retrieve key:" + keyId + ", gpg program gave exit code:" + result.exitCode + "\noutput:\n" + result.output);
	}

}
