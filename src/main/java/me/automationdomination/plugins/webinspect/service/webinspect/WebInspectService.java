package me.automationdomination.plugins.webinspect.service.webinspect;

import java.util.List;

public interface WebInspectService {
	
	public List<String> retrieveSettingsFiles();
	
	public void retrieveAndWriteScanFile(final String settings, final String scanName, final String outputPathName);

}
