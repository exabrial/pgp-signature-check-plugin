package com.github.exabrial.checkpgpsignaturesplugin;

public class PGPKey {
	public final String keyId;
	public final byte[] keyData;

	public PGPKey(final String keyId, final byte[] keyData) {
		if (keyId == null || keyData == null) {
			throw new RuntimeException("keyId and keyData must not be null");
		} else {
			this.keyId = keyId;
			this.keyData = keyData;
		}
	}

	@Override
	public String toString() {
		return "PGPKey [keyId=" + keyId + ", keyData.length=" + keyData.length + "]";
	}
}
