package com.github.exabrial.checkpgpsignaturesplugin;

import org.apache.maven.artifact.Artifact;

public interface KeyIdResolver {

	String resolveKeyIdFor(Artifact artifact);

}