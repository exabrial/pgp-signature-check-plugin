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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class PGPKeyTest {

	@Test
	public void testPGPKey_nullid() {
		final Executable executable = () -> {
			new PGPKey(null, new byte[0]);
		};
		assertThrows(IllegalArgumentException.class, executable);
	}

	@Test
	public void testPGPKey_nullbytes() {
		final Executable executable = () -> {
			new PGPKey("keyid", null);
		};
		assertThrows(IllegalArgumentException.class, executable);
	}

	@Test
	public void testToString() {
		assertNotNull(new PGPKey("keyid", new byte[0]).toString());
	}
}
