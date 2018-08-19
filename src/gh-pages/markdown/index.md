# PGP Signature Check Plugin for Maven
Maven plugin to automatically check PGP signatures of downloaded artifacts. This should be part of a multi-tier defense to prevent hostile code from being injected into your applications.

## Quick Start
Create an `artifact-key-map.txt` in your project root that pins artifacts to PGP keys. See the example [here](https://raw.githubusercontent.com/exabrial/pgp-signature-check-plugin/master/src/test/resources/artifact-key-map.txt).

```
org.bouncycastle:*:*=0x08F0AAB4D0C1A4BDDE340765B341DDB020FCB6AB
com.skip.me:*:*=skip-signature-check
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
      <version>${version.pgp-signature-check-plugin}</version>
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

`${version.pgp-signature-check-plugin}` is listed at the top of this page. Finally just run `mvn pgp-signature-check:pgp-signature-check` or your normal build process!
