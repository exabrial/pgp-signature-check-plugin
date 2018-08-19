# pgp-signature-check-plugin
Maven plugin to automatically check PGP signatures of downloaded artifacts

## Acknowledgements

This entire project was inspired by the amazing work over at https://github.com/s4u/pgpverify-maven-plugin. We borrow one class under the Apache Source License. If you're wondering the difference between the two, this project uses the native gpg on your system. This allows this plugin to handle all corner cases, such as signing using a subkey, ECDSA keys, and much more.


## Usage

Please see the docs at http://exabrial.github.io/pgp-signature-check-plugin/

But here are the Maven Coordinates:

```
<groupId>com.github.exabrial</groupId>
<artifactId>pgp-signature-check-plugin</artifactId>
```

## Building

You'll need Java8, a `~/.m2/toolchains.xml`[file](../master/support-files/toolchains.xml) setup pointing to your Java8 install, and current Maven.

### Profiles

#### sign
* `mvn clean install -P sign`
* Signs the artifacts in the build. Required for release:
  * `mvn clean release:prepare release:perform -P sign`

#### skipChecks
* `mvn clean install -P skipChecks`
* Skips all static analysis, runs unit tests

#### site

* `mvn clean site -P site`
* Reports on all static analysis. Handy to see unit test coverage

#### gh-pages

* `git checkout pgp-signature-check-plugin-1.0.X`
* `mvn clean site -P gh-pages`
* Builds the public facing website. Override the sysprop:
  * `mvn clean site -P gh-pages -Dgithub.site.skip=false` to actually deploy. You must have a server setup in `settings.xml` to do this of course.

#### eclipse

* Adds the junit5 provider to the classpath
* Set this as the default profile inside Eclipse or your IDE
