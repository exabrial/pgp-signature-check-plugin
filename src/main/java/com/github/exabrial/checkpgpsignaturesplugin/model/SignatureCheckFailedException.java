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

package com.github.exabrial.checkpgpsignaturesplugin.model;

import java.io.File;

import com.github.exabrial.checkpgpsignaturesplugin.gpg.ExecutionResult;

public class SignatureCheckFailedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SignatureCheckFailedException(final ExecutionResult result, final String keyId, final File artifactFile) {
		super("Signature failure attempting to check file:" + artifactFile.getAbsolutePath() + "with keyId:" + keyId
				+ "\nGPG exited with code:" + result.exitCode + "\noutput:\n" + result.output);
	}
}
