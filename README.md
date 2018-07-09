# pgp-signature-check-plugin
Maven plugin to automatically check PGP signatures of downloaded artifacts



## Acknowledgements

This entire project was inspired by the amazing work over at https://github.com/s4u/pgpverify-maven-plugin. We borrow one class under the Apache Source License. If you're wondering the difference between the two, this project uses the native gpg on your system. It's a bit faster than using the BouncyCastle API.