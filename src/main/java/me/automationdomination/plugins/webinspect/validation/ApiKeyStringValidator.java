package me.automationdomination.plugins.webinspect.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiKeyStringValidator implements ConfigurationValueValidator {
	
	// TODO: validate this pattern
	private final String TOKEN_PATTERN = "^[A-Za-z0-9]{40,}$";
	private final Pattern apiKeyPattern = Pattern.compile(TOKEN_PATTERN);

	@Override
	public boolean isValid(final String value) {
		final Matcher matcher = apiKeyPattern.matcher(value);
		
		return matcher.matches();
	}

}
