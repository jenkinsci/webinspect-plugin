package me.automationdomination.plugins.webinspect.validation;

public class NumericStringValidator implements ConfigurationValueValidator {

	@Override
	public boolean isValid(final String value) {
		if (value == null || value.isEmpty())
			return false;

		try {
			Integer.parseInt(value);
		} catch (final NumberFormatException e) {
			return false;
		}

		return true;
	}

}
