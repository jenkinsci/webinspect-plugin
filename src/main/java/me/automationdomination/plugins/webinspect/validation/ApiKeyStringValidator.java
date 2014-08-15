package me.automationdomination.plugins.webinspect.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiKeyStringValidator implements ConfigurationValueValidator {
	
	// TODO: validate this pattern
    //74b884e1-2c21-43e9-a9ba-7ebae7f2afea
	private final String TOKEN_PATTERN = "^[A-Za-z0-9]{8}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{12}$";
	private final Pattern apiKeyPattern = Pattern.compile(TOKEN_PATTERN);

	@Override
	public boolean isValid(final String value) {
		final Matcher matcher = apiKeyPattern.matcher(value);
		
		return matcher.matches();
	}

}
