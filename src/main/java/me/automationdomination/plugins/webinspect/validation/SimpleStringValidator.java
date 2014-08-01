package me.automationdomination.plugins.webinspect.validation;

public class SimpleStringValidator implements ConfigurationValueValidator {

	@Override
	public boolean isValid(final String value) {
		if (value == null || value.isEmpty())
			return false;

        return true;
    }

}
