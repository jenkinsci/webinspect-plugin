package me.automationdomination.plugins.webinspect.service;

public interface WebInspectServer {
	
	public String retrieveSettingsJson();
	
	public String createWebInspectScan(final String settings, final String scanName);
	
	public void waitForStatusChangeComplete(final String scanId);
	
	public byte[] retrieveScanResults(final String scanId);

}
