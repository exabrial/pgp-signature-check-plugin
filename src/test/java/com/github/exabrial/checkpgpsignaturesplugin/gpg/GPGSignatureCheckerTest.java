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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.cli.Commandline;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.exabrial.checkpgpsignaturesplugin.model.SignatureCheckFailedException;

@ExtendWith(MockitoExtension.class)
public class GPGSignatureCheckerTest {
	@InjectMocks
	private GPGSignatureChecker gpgSignatureChecker;
	@Mock
	private CommandExecutor commandExecutor;
	@Mock
	private GPGLocator gpgExecutable;
	@Mock
	private Logger logger;

	@Test
	public void checkArtifact() throws Exception {
		final String output = "gpg: Signature made Mon Jul  2 10:40:59 2018 CDT\n"
				+ "gpg:                using ECDSA key 541CEDD95256E35AC71EAB263416E5421AB9AC60\n"
				+ "gpg: using subkey 3416E5421AB9AC60 instead of primary key 0306A354336B4F0D\n" + "gpg: using pgp trust model\n"
				+ "gpg: Good signature from \"Jonathan S. Fisher <exabrial@gmail.com>\" [unknown]\n"
				+ "gpg: WARNING: This key is not certified with a trusted signature!\n"
				+ "gpg:          There is no indication that the signature belongs to the owner.\n"
				+ "Primary key fingerprint: 8716 38A2 1A7F 2C38 0664  7142 0306 A354 336B 4F0D\n"
				+ "     Subkey fingerprint: 541C EDD9 5256 E35A C71E  AB26 3416 E542 1AB9 AC60\n"
				+ "gpg: binary signature, digest algorithm SHA384, key algorithm nistp384";
		final String requiredKeyId = "871638A21A7F2C38066471420306A354336B4F0D";
		final ExecutionResult result = new ExecutionResult(0, output);
		when(commandExecutor.execute(any(Commandline.class))).thenReturn(result);
		gpgSignatureChecker.checkArtifact(mock(File.class), mock(File.class), mock(File.class), requiredKeyId);
		verify(commandExecutor).execute(any(Commandline.class));
	}

	@Test
	public void checkArtifact_failedSignature_badViaExitCode() throws Exception {
		final Executable executable = () -> {
			final String output = "	gpg: Signature made Mon Jul  2 10:40:59 2018 CDT\n"
					+ "	gpg:                using ECDSA key 541CEDD95256E35AC71EAB263416E5421AB9AC60\n"
					+ "	gpg: using subkey 3416E5421AB9AC60 instead of primary key 0306A354336B4F0D\n" + "	gpg: using pgp trust model\n"
					+ "	gpg: BAD signature from \"Jonathan S. Fisher <exabrial@gmail.com>\" [unknown]\n"
					+ "	gpg: binary signature, digest algorithm SHA384, key algorithm nistp384";
			final String requiredKeyId = "871638A21A7F2C38066471420306A354336B4F0D";
			final ExecutionResult result = new ExecutionResult(2, output);
			when(commandExecutor.execute(any(Commandline.class))).thenReturn(result);
			gpgSignatureChecker.checkArtifact(mock(File.class), mock(File.class), mock(File.class), requiredKeyId);
		};
		assertThrows(SignatureCheckFailedException.class, executable);
	}

	@Test
	public void checkArtifact_failedSignature_badViaStdOut() throws Exception {
		final Executable executable = () -> {
			final String output = "	gpg: Signature made Mon Jul  2 10:40:59 2018 CDT\n"
					+ "	gpg:                using ECDSA key 541CEDD95256E35AC71EAB263416E5421AB9AC60\n"
					+ "	gpg: using subkey 3416E5421AB9AC60 instead of primary key 0306A354336B4F0D\n" + "	gpg: using pgp trust model\n"
					+ "	gpg: BAD signature from \"Jonathan S. Fisher <exabrial@gmail.com>\" [unknown]\n"
					+ "	gpg: binary signature, digest algorithm SHA384, key algorithm nistp384";
			final String requiredKeyId = "871638A21A7F2C38066471420306A354336B4F0D";
			final ExecutionResult result = new ExecutionResult(0, output);
			when(commandExecutor.execute(any(Commandline.class))).thenReturn(result);
			gpgSignatureChecker.checkArtifact(mock(File.class), mock(File.class), mock(File.class), requiredKeyId);
		};
		assertThrows(SignatureCheckFailedException.class, executable);
	}

	@Test
	public void testIsGoodOutput_DSAPrimaryKey() {
		final String output = "gpg: armor header: Version: GnuPG v1\n" + "gpg: Signature made Wed Jan  3 21:28:16 2018 CST\n"
				+ "gpg:                using DSA key B341DDB020FCB6AB\n" + "gpg: using pgp trust model\n"
				+ "gpg: Good signature from \"The Legion of the Bouncy Castle (Maven Repository Artifact Signer) <bcmavensync@bouncycastle.org>\" [unknown]\n"
				+ "gpg: WARNING: This key is not certified with a trusted signature!\n"
				+ "gpg:          There is no indication that the signature belongs to the owner.\n"
				+ "Primary key fingerprint: 08F0 AAB4 D0C1 A4BD DE34  0765 B341 DDB0 20FC B6AB\n"
				+ "gpg: binary signature, digest algorithm SHA1, key algorithm dsa1024";
		final String requiredKeyId = "08F0AAB4D0C1A4BDDE340765B341DDB020FCB6AB";
		assertTrue(GPGSignatureChecker.isGoodOutput(output, requiredKeyId));
	}

	@Test
	public void testIsGoodOutput_ECDSAsubkey() {
		final String output = "gpg: Signature made Mon Jul  2 10:40:59 2018 CDT\n"
				+ "gpg:                using ECDSA key 541CEDD95256E35AC71EAB263416E5421AB9AC60\n"
				+ "gpg: using subkey 3416E5421AB9AC60 instead of primary key 0306A354336B4F0D\n" + "gpg: using pgp trust model\n"
				+ "gpg: Good signature from \"Jonathan S. Fisher <exabrial@gmail.com>\" [unknown]\n"
				+ "gpg: WARNING: This key is not certified with a trusted signature!\n"
				+ "gpg:          There is no indication that the signature belongs to the owner.\n"
				+ "Primary key fingerprint: 8716 38A2 1A7F 2C38 0664  7142 0306 A354 336B 4F0D\n"
				+ "     Subkey fingerprint: 541C EDD9 5256 E35A C71E  AB26 3416 E542 1AB9 AC60\n"
				+ "gpg: binary signature, digest algorithm SHA384, key algorithm nistp384";
		final String requiredKeyId = "871638A21A7F2C38066471420306A354336B4F0D";
		assertTrue(GPGSignatureChecker.isGoodOutput(output, requiredKeyId));
	}

	@Test
	public void testIsGoodOutput_subkeyMismatch() {
		final String output = "gpg: Signature made Mon Jul  2 10:40:59 2018 CDT\n"
				+ "gpg:                using ECDSA key aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\n"
				+ "gpg: using subkey 3416E5421AB9AC60 instead of primary key 0306A354336B4F0D\n" + "gpg: using pgp trust model\n"
				+ "gpg: Good signature from \"Jonathan S. Fisher <exabrial@gmail.com>\" [unknown]\n"
				+ "gpg: WARNING: This key is not certified with a trusted signature!\n"
				+ "gpg:          There is no indication that the signature belongs to the owner.\n"
				+ "Primary key fingerprint: 8716 38A2 1A7F 2C38 0664  7142 0306 A354 336B 4F0D\n"
				+ "     Subkey fingerprint: 541C EDD9 5256 E35A C71E  AB26 3416 E542 1AB9 AC60\n"
				+ "gpg: binary signature, digest algorithm SHA384, key algorithm nistp384";
		final String requiredKeyId = "871638A21A7F2C38066471420306A354336B4F0D";
		assertFalse(GPGSignatureChecker.isGoodOutput(output, requiredKeyId));
	}

	@Test
	public void testIsGoodOutput_masterMismatch() {
		final String output = "gpg: Signature made Mon Jul  2 10:40:59 2018 CDT\n"
				+ "gpg:                using ECDSA key 541CEDD95256E35AC71EAB263416E5421AB9AC60\n"
				+ "gpg: using subkey 3416E5421AB9AC60 instead of primary key aaaaaaaaaaaaaaaa\n" + "gpg: using pgp trust model\n"
				+ "gpg: Good signature from \"Jonathan S. Fisher <exabrial@gmail.com>\" [unknown]\n"
				+ "gpg: WARNING: This key is not certified with a trusted signature!\n"
				+ "gpg:          There is no indication that the signature belongs to the owner.\n"
				+ "Primary key fingerprint: 8716 38A2 1A7F 2C38 0664  7142 0306 A354 336B 4F0D\n"
				+ "     Subkey fingerprint: 541C EDD9 5256 E35A C71E  AB26 3416 E542 1AB9 AC60\n"
				+ "gpg: binary signature, digest algorithm SHA384, key algorithm nistp384";
		final String requiredKeyId = "871638A21A7F2C38066471420306A354336B4F0D";
		assertFalse(GPGSignatureChecker.isGoodOutput(output, requiredKeyId));
	}

	@Test
	public void testIsGoodOutput_tamperedFile() {
		final String output = "	gpg: Signature made Mon Jul  2 10:40:59 2018 CDT\n"
				+ "	gpg:                using ECDSA key 541CEDD95256E35AC71EAB263416E5421AB9AC60\n"
				+ "	gpg: using subkey 3416E5421AB9AC60 instead of primary key 0306A354336B4F0D\n" + "	gpg: using pgp trust model\n"
				+ "	gpg: BAD signature from \"Jonathan S. Fisher <exabrial@gmail.com>\" [unknown]\n"
				+ "	gpg: binary signature, digest algorithm SHA384, key algorithm nistp384";
		assertFalse(GPGSignatureChecker.isGoodOutput(output, "871638A21A7F2C38066471420306A354336B4F0D"));
	}

	@Test
	public void testIsGoodOutput_nonGpgOutputReturnsFalse() {
		final String output = "not gpg output";
		assertFalse(GPGSignatureChecker.isGoodOutput(output, "871638A21A7F2C38066471420306A354336B4F0D"));
	}

	@Test
	public void testIsGoodOutput_exceptionsResultInFalse() {
		assertFalse(GPGSignatureChecker.isGoodOutput(null, null));
	}
}
