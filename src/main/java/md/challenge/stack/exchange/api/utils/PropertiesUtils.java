package md.challenge.stack.exchange.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtils {

	public static Properties loadProperties(String propertiesFilename) {
		Properties prop = new Properties();

		try (InputStream input = PropertiesUtils.class.getClassLoader().getResourceAsStream(propertiesFilename)) {

			if (input == null) {
				System.err.println("Unable to find " + propertiesFilename + " file ");
			}

			prop.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return prop;
	}

}