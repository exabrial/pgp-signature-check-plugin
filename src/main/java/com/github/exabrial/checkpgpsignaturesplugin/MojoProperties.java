package com.github.exabrial.checkpgpsignaturesplugin;

import java.util.Properties;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Maven dependency injection is a joke. It's impossible to inject a parameter
 * into a sub-component. So here we are, doing terrible things to perfectly good
 * JVMs who don't deserve it.
 */
@Named
@Singleton
public class MojoProperties {
	private final Properties properties = new Properties();

	public String getProperty(final String key) {
		return properties.getProperty(key);
	}

	void setProperty(final String key, final String value) {
		properties.setProperty(key, value);
	}
}
