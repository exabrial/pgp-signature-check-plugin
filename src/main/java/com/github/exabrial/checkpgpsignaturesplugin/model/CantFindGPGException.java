package com.github.exabrial.checkpgpsignaturesplugin.model;

import com.github.exabrial.checkpgpsignaturesplugin.gpg.ExecutionResult;

public class CantFindGPGException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CantFindGPGException(final ExecutionResult result) {
		super(result.toString());
	}
}
