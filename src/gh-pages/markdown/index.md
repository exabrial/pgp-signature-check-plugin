# pgp-signature-check-plugin
Maven plugin to automatically check PGP signatures of downloaded artifacts. This should be part of a multi-tier defense to prevent hostile code from being injected into your applications.

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
      <version>${version.pgp-signature-check}</version>
      <executions>
        <execution>
          <id>pgp-signature-check</id>
          <goals>
            <goal>pgp-signature-check</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
...
  </plugins>
...
</build>
```

For the value of `${version.pgp-signature-check}`, the latest release version is:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.exabrial/pgp-signature-check-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.exabrial/pgp-signature-check-plugin)



Finally just run `mvn pgp-signature-check:pgp-signature-check` or your normal build process!