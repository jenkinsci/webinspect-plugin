package me.automationdomination.plugins.webinspect.service.ssc;

/**
 * Created with IntelliJ IDEA.
 * User: bspruth
 * Date: 8/29/14
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */
import org.junit.Assert;
import org.junit.Test;

public class CmdLineFortifyClientManualTest {

	//@Test
	public void retriveProjectsTest() {
		final String fortifyClient = "/Applications/HP_Fortify/HP_Fortify_SCA_and_Apps_4.10/bin/fortifyclient";
		final String sscUrl = "https://packetnoodle.automationdomination.me/ssc";
		final String sscToken = "41ff726a-1b08-4a77-8338-c77b1646c271";
		
		final SscServer sscServer = new CmdLineFortifyClientImpl(fortifyClient, sscUrl, sscToken);
		
		Assert.assertEquals("2,ECOMMERCE,commerce4j\n4,EDUCATION,WebGoat-v5.4\n3,OFFICE,soplanning\n", sscServer.retrieveProjects());
	}

}
