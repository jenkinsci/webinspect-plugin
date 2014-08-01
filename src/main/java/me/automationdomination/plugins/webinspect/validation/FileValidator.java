package me.automationdomination.plugins.webinspect.validation;

import java.io.File;

public class FileValidator implements ConfigurationValueValidator {

	@Override
	public boolean isValid(final String value) {
		if (value == null || value.isEmpty())
			return false;
		
		final File file = new File(value);
		
		if (!file.exists())
			return false;
		
		return true;
	}

}
