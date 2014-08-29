package me.automationdomination.plugins.webinspect.service;

import java.util.List;

import org.junit.Assert;

public class WebInspectServiceManualTest {
	
	// @Test
	public void retrieveSettingsTest() {
		final WebInspectServer webInspectServer = new WebInspectServerImpl("http://192.168.56.101:8083/webinspect/scanner");
		final WebInspectService webInspectService = new WebInspectServiceImpl(webInspectServer);
		final List<String> settings = webInspectService.retrieveSettings();
		Assert.assertEquals(3, settings.size());
		Assert.assertTrue(settings.contains("Default"));
		Assert.assertTrue(settings.contains("settings1"));
		Assert.assertTrue(settings.contains("settings2"));
	}
	
	// @Test
	public void retrieveScanFileTest() {
		final WebInspectServer webInspectServer = new WebInspectServerImpl("http://192.168.56.101:8083/webinspect/scanner");
		final WebInspectService webInspectService = new WebInspectServiceImpl(webInspectServer);
		webInspectService.retrieveAndWriteScanFile("settings1", "TEST7", "/tmp/TEST7.fpr");
	}

}
