package me.automationdomination.plugins.webinspect.service.ssc;

import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

public class SscServiceImplTest {
	
	@Test
	public void retrieveProjectsTest() {
		final SscServer sscServer = EasyMock.createMock(SscServer.class);
		EasyMock.expect(sscServer.retrieveProjects()).andReturn("2,ECOMMERCE,commerce4j\n4,EDUCATION,WebGoat-v5.4\n3,OFFICE,soplanning\n");
		EasyMock.replay(sscServer);
		
		final SscService sscService = new SscServiceImpl(sscServer);
		final Map<Integer, String> projects = sscService.retrieveProjects();
		
		Assert.assertEquals(3, projects.size());
		
		Assert.assertTrue(projects.containsKey(2));
		Assert.assertTrue(projects.containsKey(3));
		Assert.assertTrue(projects.containsKey(4));
		
		Assert.assertEquals("ECOMMERCE (commerce4j)", projects.get(2));
		Assert.assertEquals("OFFICE (soplanning)", projects.get(3));
		Assert.assertEquals("EDUCATION (WebGoat-v5.4)", projects.get(4));
	}

}
