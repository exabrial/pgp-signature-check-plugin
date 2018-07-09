package com.github.exabrial.checkpgpsignaturesplugin.file;

import java.io.File;

public class UserSpecifiedKeyCacheDirectoryDoesntExistException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UserSpecifiedKeyCacheDirectoryDoesntExistException(final File keyCacheDirectory) {
		super("User specified keyCacheDirectory does not exist:" + keyCacheDirectory.getAbsolutePath());
	}
}
