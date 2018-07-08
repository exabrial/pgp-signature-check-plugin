package com.github.exabrial.checkpgpsignaturesplugin;

import java.io.File;

public interface KeysCache {

	File getKeyLocation(String requiredKeyId);

	File put(PGPKey pgpKey);

}