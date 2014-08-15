package me.automationdomination.plugins.webinspect.service;

import hudson.EnvVars;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinuxEnvironmentVariableParsingService implements EnvironmentVariableParsingService {

    /* TODO: neeed to add compatability for windows
    * extending on EnvVars rather than a replace all
    * https://github.com/jenkinsci/email-ext-plugin/blob/master/src/main/java/hudson/plugins/emailext/EmailRecipientUtils.java
    */
    private final Pattern environmentVariablePattern = Pattern.compile("\\$\\{.+?\\}");

    @Override
    public String parseEnvironentVariables(final EnvVars envVars, final String value) {
        final Matcher matcher = environmentVariablePattern.matcher(value);

        String parsedValue = value;

        while (matcher.find()) {
            final String matchedValue = matcher.group();

            // TODO: can this be done more efficiently?
            final String environmentVariableKey = matchedValue.replaceAll("\\$\\{", "").replaceAll("\\}", "");

            final String environmentVariableValue = envVars.get(environmentVariableKey);

            // if this is null, that means the environment variable was not found
            if (environmentVariableValue != null) {
                // TODO: can this be done more efficiently?
                parsedValue = parsedValue.replaceAll("\\$\\{" + environmentVariableKey + "\\}", environmentVariableValue);
            }
        }

        return parsedValue;
    }

}
