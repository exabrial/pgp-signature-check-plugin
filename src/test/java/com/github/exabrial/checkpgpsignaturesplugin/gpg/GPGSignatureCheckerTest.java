package com.github.exabrial.checkpgpsignaturesplugin.gpg;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.exabrial.checkpgpsignaturesplugin.gpg.GPGSignatureChecker;

public class GPGSignatureCheckerTest {

	@Test
	public void testIsGoodOutputECDSA() {
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
	public void testIsGoodOutputDSA() {
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

}
