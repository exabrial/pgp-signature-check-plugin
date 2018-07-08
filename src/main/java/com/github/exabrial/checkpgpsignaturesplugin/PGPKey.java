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
