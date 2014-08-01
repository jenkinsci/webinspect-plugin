package me.automationdomination.plugins.webinspect.validation;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApacheCommonsUrlValidator implements ConfigurationValueValidator {

	private static final Logger logger = LoggerFactory.getLogger(ApacheCommonsUrlValidator.class);

	private final UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" }, UrlValidator.ALLOW_LOCAL_URLS);

	@Override
	public boolean isValid(final String value) {
		if (logger.isDebugEnabled()) logger.debug("checking url <" + value + ">");

		boolean isValid = true;

		if (value == null || value.length() == 0) {
			isValid = false;
		} else if (!urlValidator.isValid(value)) {
			isValid = false;
		}

		if (logger.isDebugEnabled()) logger.debug("url <" + value + "> is <" + (isValid ? "VALID" : "INVALID") + ">");

		return isValid;
	}

}
