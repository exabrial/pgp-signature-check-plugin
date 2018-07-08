package com.github.exabrial.checkpgpsignaturesplugin;

public interface KeyRetriever {

	PGPKey retrieveKey(String requiredKeyId);

}