package ch.voulgarakis.icsc2018.recruitment.config;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;

import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

/**
 * Help load configuration from an external source (ie a DB)
 */
// @Configuration
public class CustomPropertySourceLocator implements PropertySourceLocator {

	/**
	 * Let's find a random port number which is not in use.
	 */
	@Override
	public PropertySource<?> locate(Environment environment) {
		// Port 80xx
		for (int i = 0; i < 1000; i++) {
			int port = 80 + Double.valueOf(100 * Math.random()).intValue();
			if (available(port))
				return new MapPropertySource("customProperty", Collections.<String, Object> singletonMap("PORT", port));
		}
		throw new RuntimeException("Could not find a free PORT to start the server.");
	}

	private static boolean available(int port) {
		try (Socket ignored = new Socket("localhost", port)) {
			return false;
		} catch (IOException ignored) {
			return true;
		}
	}
}