package com.github.exabrial.checkpgpsignaturesplugin;

import java.io.File;

public interface SignatureChecker {

	void checkArtifact(File artifactFile, File signatureFile, File keyRingFile, String requiredKeyId);

}