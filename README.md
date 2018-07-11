# pgp-signature-check-plugin
Maven plugin to automatically check PGP signatures of downloaded artifacts

## Acknowledgements

This entire project was inspired by the amazing work over at https://github.com/s4u/pgpverify-maven-plugin. We borrow one class under the Apache Source License. If you're wondering the difference between the two, this project uses the native gpg on your system. This allows this plugin to handle all corner cases, such as signing using a subkey, ECDSA keys, and much more.


## Usage

Create an `artifact-key-map.txt` in your project root that pins artifacts to PGP keys. Wildcards are allowed. The syntax is `groupid:artifactid:version=0xPGPkeyfingerprint`. See the example [here](../master/src/test/resources/artifact-key-map.txt).

```
org.bouncycastle:*:*=0x08F0AAB4D0C1A4BDDE340765B341DDB020FCB6AB
org.junit.*:*:*=0xFF6E2C001948C5F2F38B0CC385911F425EC61B51
org.opentest4j:*:*=0xFF6E2C001948C5F2F38B0CC385911F425EC61B51
```

Next add this to your build section:

```
<build>
...
	<plugins>
...
		<plugin>
			<groupId>com.github.exabrial</groupId>
			<artifactId>pgp-signature-check-plugin</artifactId>
			<version>1.0.0</version>
			<executions>
				<execution>
					<id>pgp-signature-check</id>
					<goals>
						<goal>pgp-signature-check</goal>
					</goals>
				</execution>
			</executions>
		</plugin>
	</plugins>
</build>
```

Finally just run `mvn pgp-signature-check:pgp-signature-check` or your normal build process!


## Building

You'll need Java8, a `~/.m2/toolchains.xml`[file](../master/support-files/toolchains.xml) setup pointing to your Java8 install, and current Maven.

`mvn clean install -P skipChecks` use the `skipChecks` profile to skip static analysis. 

