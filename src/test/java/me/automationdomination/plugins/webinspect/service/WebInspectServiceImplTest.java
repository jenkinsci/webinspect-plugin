package me.automationdomination.plugins.webinspect.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class WebInspectServiceImplTest {
	
	//@Test
	public void test() {
		final WebInspectServiceImpl webInspectServiceImpl = new WebInspectServiceImpl("http://192.168.56.101:8083/webinspect/scanner");
		final List<String> settings = webInspectServiceImpl.retrieveSettings();
		Assert.assertEquals(3, settings.size());
		Assert.assertTrue(settings.contains("Default"));
		Assert.assertTrue(settings.contains("settings1"));
		Assert.assertTrue(settings.contains("settings2"));
	}

}
