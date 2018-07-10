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

package com.github.exabrial.checkpgpsignaturesplugin.gpg;

public class ExecutionResult {
	public final int exitCode;
	public final String output;

	ExecutionResult(final int exitCode, final String output) {
		this.exitCode = exitCode;
		this.output = output;
	}

	@Override
	public String toString() {
		return "ExecutionResult [exitCode=" + exitCode + "\n, output=\n" + output + "]";
	}
}
