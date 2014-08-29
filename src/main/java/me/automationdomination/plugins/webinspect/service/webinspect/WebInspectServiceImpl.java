package me.automationdomination.plugins.webinspect.service.webinspect;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebInspectServiceImpl implements WebInspectService {
	
	private static final Logger logger = Logger.getLogger(WebInspectServiceImpl.class.getName());

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final WebInspectServer webInspectServer;
	
	public WebInspectServiceImpl(final WebInspectServer webInspectServer) {
		this.webInspectServer = webInspectServer;
	}

	@Override
	public List<String> retrieveSettings() {
		logger.info("retrieving settings");
		
		final String settingsJson = webInspectServer.retrieveSettingsJson();
		
		final List<String> settings;
		
		try {
			settings = objectMapper.readValue(settingsJson, new TypeReference<List<String>>(){});
		} catch (final JsonParseException e) {
			throw new RuntimeException("JsonParseException unmarshalling webinspect settings json", e);
		} catch (final JsonMappingException e) {
			throw new RuntimeException("JsonMappingException unmarshalling webinspect settings json", e);
		} catch (final IOException e) {
			throw new RuntimeException("IOException unmarshalling webinspect settings json", e);
		}
		
		return settings;		
	}
	
	@Override
	public void retrieveAndWriteScanFile(final String settings, final String scanName, final String outputPathName) {
		logger.info("retriving scan file with settings <" + settings + "> and scan name <" + scanName + "> and writing to <" + outputPathName + ">");
		
		final String scanIdJson = webInspectServer.createWebInspectScan(settings, scanName);
		
		final String scanId = extractScanId(scanIdJson);
		
		webInspectServer.waitForStatusChangeComplete(scanId);
		
		final byte[] scanResults = webInspectServer.retrieveScanResults(scanId);
		
		writeScanResults(scanResults, outputPathName);
	}

	private String extractScanId(final String scanIdJson) {
		logger.info("extracting scan id from json");
		
		final Map<String, String> scanIdResponse;
		
		try {
			scanIdResponse = objectMapper.readValue(scanIdJson, new TypeReference<Map<String, String>>(){});
		} catch (final JsonParseException e) {
			throw new RuntimeException("JsonParseException unmarshalling webinspect scan id json", e);
		} catch (final JsonMappingException e) {
			throw new RuntimeException("JsonMappingException unmarshalling webinspect scan id json", e);
		} catch (final IOException e) {
			throw new RuntimeException("IOException unmarshalling webinspect scan id json", e);
		}
		
		if (!scanIdResponse.containsKey("ScanId")) {
			throw new RuntimeException("ScanId missing from webinspect scan id json");
		}
		
		final String scanId = scanIdResponse.get("ScanId");
		
		return scanId;
	}
	
	private void writeScanResults(final byte[] scanResults, final String outputPathName) {
		logger.info("writing <" + scanResults.length + "> to <" + outputPathName + ">");

		final Path path = Paths.get(outputPathName);
		
		try {
			Files.write(path, scanResults);
		} catch (final IOException e) {
			throw new RuntimeException("exception writing scan results to <" + outputPathName + ">", e);
		}
	}

}
