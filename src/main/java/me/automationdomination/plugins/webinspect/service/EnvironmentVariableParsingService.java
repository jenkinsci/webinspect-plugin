package me.automationdomination.plugins.webinspect.service;

import hudson.EnvVars;

public interface EnvironmentVariableParsingService {

    public String parseEnvironentVariables(EnvVars paramEnvVars, String paramString);

}
