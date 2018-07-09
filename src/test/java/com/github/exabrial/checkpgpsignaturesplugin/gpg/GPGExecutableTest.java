package com.github.exabrial.checkpgpsignaturesplugin.gpg;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import javax.inject.Provider;

import org.codehaus.plexus.logging.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GPGExecutableTest {
	@InjectMocks
	private GPGExecutable gpgExecutable;
	@Mock
	private Logger logger;
	@Mock
	private Provider<String> gpgExecutableProvider;

	@Test
	public void testPostConstruct() {
		final String yeeyee = "YEE YEE - Earl Dibbles Jr";
		when(gpgExecutableProvider.get()).thenReturn(yeeyee);
		gpgExecutable.postConstruct();
		assertEquals(yeeyee, gpgExecutable.getGPGExecutable());
	}
}
