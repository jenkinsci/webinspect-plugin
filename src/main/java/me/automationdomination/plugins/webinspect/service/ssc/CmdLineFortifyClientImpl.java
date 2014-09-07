package me.automationdomination.plugins.webinspect.service.ssc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA. User: bspruth Date: 8/14/14 Time: 6:55 PM To
 * change this template use File | Settings | File Templates.
 */
public class CmdLineFortifyClientImpl implements SscServer {
	
	private static Logger logger = Logger.getLogger(CmdLineFortifyClientImpl.class.getName());

	private String fortifyClient;
	private String sscUrl;
	private String sscToken;

	public CmdLineFortifyClientImpl(
			final String fortifyClient, 
			final String sscUrl,
			final String sscToken) {
		this.fortifyClient = fortifyClient;
		this.sscUrl = sscUrl;
		this.sscToken = sscToken;
	}

	@Override
	public String retrieveProjects() {
		final String[] command = { fortifyClient, "listProjectVersions", "-machineoutput", "-authtoken", sscToken, "-url", sscUrl };
		
		// run the command
		final Process fortifyClientProcess;
		
		try {
			fortifyClientProcess = Runtime.getRuntime().exec(command);
		} catch (final IOException e) {
			logger.warning("IOException running fortifyclient command to retrieve projects");
			throw new RuntimeException("IOException running fortifyclient command to retrieve projects", e);
		}
		
		final int exitValue;
		
		try {
			exitValue = fortifyClientProcess.waitFor();
		} catch (final InterruptedException e) {
			logger.warning("InterruptedException running fortifyclient command to retrieve projects");
			throw new RuntimeException("InterruptedException running fortifyclient command to retrieve projects", e);
		}
		
		if (exitValue == 0) {
			// read the output
			final BufferedReader fortifyClientProcessOutputReader = new BufferedReader(
					new InputStreamReader(fortifyClientProcess.getInputStream()));
			
			
			final StringBuilder projectsStringBuilder = new StringBuilder();
			
			// print output
			String line;
			
			try {
				while ((line = fortifyClientProcessOutputReader.readLine()) != null) {
					projectsStringBuilder.append(line);
					projectsStringBuilder.append("\n");
				}
			} catch (final IOException e) {
				logger.warning("IOException reading output of fortifyclient command to retrieve projects");
				throw new RuntimeException("IOException reading output of fortifyclient command to retrieve projects", e);
			}
			
			return projectsStringBuilder.toString();
		} else {
			throw new RuntimeException("ssc service not available");
		}
	}

    public void uploadFpr(File fprScanFile, String projectID) {
        String[] command = {fortifyClient, "uploadFPR", "-machineoutput", "-file", fprScanFile.getAbsolutePath(), "-projectVersionID", projectID,
                "-authtoken", sscToken, "-url", sscUrl};

        // run the command
        final Process fortifyClientProcess;

        try {
            fortifyClientProcess = Runtime.getRuntime().exec(command);
        } catch (final IOException e) {
            logger.warning("IOException running fortifyclient command to upload FPR");
            throw new RuntimeException("IOException running fortifyclient command to upload FPR", e);
        }
        /*
        try {
            FortifyClientProcessFprUploader fortifyClientProcessFprUploader = new fortifyClientProcessFprUploader(sscUrl, sscToken, fortifyClient, fprFilePath);
            File fprScanFile = uploadFprFile(fprFilePath);
            logger.info("FPR upload begin: Uploading " + fprFile + "" + " to " + sscUrl + " for project id " + projectID );
            fortifyCli
            //(fprFile, projectID);
        } catch (FPRNotFoundException e) {
            logger.warning("FPR upload failed: ";
            return TaskResultBuilder.create(taskContext).failed().build();
        }

        buildLogger.addBuildLogEntry("FPR upload success: Upload complete");
        return TaskResultBuilder.create(taskContext).success().build();
    }
     */
    }
	
	// 9/3/2014: to be implemented
	@SuppressWarnings("unused")
	private void validateFortifyConnection(String sscUrl,
			String sscToken, String fortifyClient) {
		String[] command = { fortifyClient, "listProjectVersions",
				"-machineoutput", "-authtoken", sscToken, "-url", sscUrl };
		return;
	}

	// 9/3/2014: to be implemented
	// added for fpr
	@SuppressWarnings("unused")
	private void validateFortifyFPRPath(String fortifyFPRPath)
			throws IOException {
		File fortifyFPR = new File(fortifyFPRPath);
		if (!fortifyFPR.exists()) {
			throw new IOException("That is not a valid Fortify FPR path");
		}

	}

}
