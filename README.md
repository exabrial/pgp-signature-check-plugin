# pgp-signature-check-plugin
Maven plugin to automatically check PGP signatures of downloaded artifacts

## Acknowledgements

This entire project was inspired by the amazing work over at https://github.com/s4u/pgpverify-maven-plugin. We borrow one class under the Apache Source License. If you're wondering the difference between the two, this project uses the native gpg on your system. This allows this plugin to handle all corner cases, such as signing using a subkey, ECDSA keys, and much more. 

## Status

I don't want to release until I have 100% test coverage. However, the plugin DOES work right now and is 100% usable

## Building

You'll need Java8, a `~/.m2/toolchains.xml`[file](../master/support-files/toolchains.xml) setup pointing to your Java8 install, and current Maven.

`mvn clean install -P skipChecks` use the `skipChecks` profile to skip static analysis. 

## Usage

Create an `artifact-key-map.txt` in your project root. See the example [here](../master/src/test/resources/artifact-key-map.txt)

```
<build>
	<plugins>
		<plugin>
			<groupId>com.github.exabrial</groupId>
			<artifactId>pgp-signature-check-plugin</artifactId>
			<version>1.0.0-SNAPSHOT</version>
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
