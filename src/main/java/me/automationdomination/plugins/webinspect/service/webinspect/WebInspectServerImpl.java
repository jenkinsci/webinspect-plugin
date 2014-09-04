package me.automationdomination.plugins.webinspect.service.webinspect;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class WebInspectServerImpl implements WebInspectServer {
	
	private static final Logger logger = Logger.getLogger(WebInspectServerImpl.class.getName());
	
	private final HttpClient httpClient;
	private final String webInspectServerUrl;
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	public WebInspectServerImpl(final String webInspectServerUrl) {
		this(webInspectServerUrl, 600000);
	}
	
	public WebInspectServerImpl(final String webInspectServerUrl, final int socketTimeout) {
		this.httpClient = HttpClientBuilder.create()
				.setDefaultRequestConfig(RequestConfig.custom()
						.setSocketTimeout(socketTimeout)
						.build())
				.build();
		
		this.webInspectServerUrl = webInspectServerUrl;
	}

	@Override
	public String retrieveSettingsJson() {
		logger.info("retrieving settings json");
		
		final HttpResponse httpResponse = executeSettingsJsonRequest();
		
		validateResponse(httpResponse, HttpStatus.SC_OK);
		
		return extractSettingsJson(httpResponse);
	}

	private HttpResponse executeSettingsJsonRequest() {
		logger.info("executing settings json request");
		
		final HttpResponse httpResponse;
		
		try {
			httpResponse = httpClient.execute(new HttpGet(webInspectServerUrl + "/settings"));
		} catch (final ClientProtocolException e) {
			throw new RuntimeException("ClientProtocolException while executing settings json request", e);
		} catch (final IOException e) {
			throw new RuntimeException("ClientProtocolException while executing settings json request", e);
		}
		
		return httpResponse;
	}
	
	private String extractSettingsJson(final HttpResponse httpResponse) {
		logger.info("extracting settings json");
		
		final HttpEntity httpEntity = httpResponse.getEntity();
		
		final String settingsJson;
		
		try {
			settingsJson = EntityUtils.toString(httpEntity);
		} catch (final ParseException e) {
			throw new RuntimeException("ParseException while extracting settings json", e);
		} catch (final IOException e) {
			throw new RuntimeException("ParseException while extracting settings json", e);
		}
		
		return settingsJson;
	}	

	@Override
	public String createWebInspectScan(final String settings, final String scanName) {
		logger.info("creating webinspect scan with settings <" + settings + "> and scan name <" + scanName + ">");
		
		final HttpResponse httpResponse = executeWebInspectScanRequest(settings, scanName);
		
		validateResponse(httpResponse, HttpStatus.SC_CREATED);
		
		return extractScanIdJson(httpResponse);
	}
	
	private HttpResponse executeWebInspectScanRequest(final String settings, final String scanName) {
		logger.info("executing webinspect scan request with settings <" + settings + "> and scan name <" + scanName + ">");
		
		final HttpResponse httpResponse;
		
		final String overridesString = generateOverridesString(scanName);
		
		final HttpPost httpPost = new HttpPost(webInspectServerUrl);
		httpPost.setEntity(new StringEntity("settingsName=" + settings + "&overrides=" + overridesString, Consts.UTF_8));
		
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (final ClientProtocolException e) {
			throw new RuntimeException("ClientProtocolException while executing scan request with settings <" + settings + "> and scan name <" + scanName + ">", e);
		} catch (final IOException e) {
			throw new RuntimeException("IOException while executing scan request with settings <" + settings + "> and scan name <" + scanName + ">", e);
		}
		
		return httpResponse;
	}
	
	private String generateOverridesString(final String scanName) {
		final Map<String, String> overrides = new HashMap<>();
		overrides.put("ScanName", scanName);
		
		final StringWriter overridesStringWriter = new StringWriter();
		
		try {
			objectMapper.writeValue(overridesStringWriter, overrides);
		} catch (final JsonGenerationException e) {
			throw new RuntimeException("JsonGenerationException marshalling scan name request", e);
		} catch (final JsonMappingException e) {
			throw new RuntimeException("JsonMappingException marshalling scan name request", e);
		} catch (final IOException e) {
			throw new RuntimeException("IOException marshalling scan name request", e);
		}
		
		overridesStringWriter.flush();
		
		final String overridesString = overridesStringWriter.toString();
		
		return overridesString;
	}
	
	private String extractScanIdJson(final HttpResponse httpResponse) {
		logger.info("extracting scan id json");
		
		final HttpEntity httpEntity = httpResponse.getEntity();

		final String scanIdJson;
		
		try {
			scanIdJson = EntityUtils.toString(httpEntity);
		} catch (final ParseException e) {
			throw new RuntimeException("ParseException while extracting scan id json", e);
		} catch (final IOException e) {
			throw new RuntimeException("IOException while extracting scan id json", e);
		}
		
		return scanIdJson;
	}

	@Override
	public void waitForStatusChangeComplete(final String scanId) {
		logger.info("waiting for scan id <" + scanId + "> with waitforstatuschange");
		
		final URIBuilder uriBuilder;
		// Changed to http://192.168.1.4:8083/webinspect/scanner/123-1234-12123-12324?action=waitforstatuschange
        // from http://192.168.1.4:8083/webinspect/scanner/123-1234-12123-12324?WaitForStatusChange=Complete
		try {
			uriBuilder = new URIBuilder(webInspectServerUrl + "/" + scanId)
				.setParameter("action", "waitforstatuschange");
		} catch (final URISyntaxException e) {
			throw new RuntimeException("URISyntaxException while waiting for waitforstatuschange on <" + scanId + ">", e);
		}
		
		// TODO: check the return code here
		try {
			httpClient.execute(new HttpGet(uriBuilder.build()));
		} catch (final ClientProtocolException e) {
			throw new RuntimeException("ClientProtocolException while waiting for scan id <" + scanId + "> to complete", e);
		} catch (final IOException e) {
			throw new RuntimeException("IOException while waiting for scan id <" + scanId + "> to complete", e);
		} catch (final URISyntaxException e) {
			throw new RuntimeException("URISyntaxException while waiting for scan id <" + scanId + "> to complete", e);
		}
	}

	@Override
	public byte[] retrieveScanResults(final String scanId) {
		logger.info("retriving scan results for scan id <" + scanId + ">");
		
		final HttpResponse httpResponse = executeScanResultsRequest(scanId);
		
		validateResponse(httpResponse, HttpStatus.SC_OK);
		
		return extractScanResultsData(httpResponse);
	}
	
	private HttpResponse executeScanResultsRequest(final String scanId) {
		logger.info("executing scan results request for scan id <" + scanId + ">");
		
		final HttpResponse httpResponse;
		
		try {
			final URIBuilder waitForStatusChangeBuilder = new URIBuilder(webInspectServerUrl + "/" + scanId + ".fpr");
			httpResponse = httpClient.execute(new HttpGet(waitForStatusChangeBuilder.build()));
		} catch (final ClientProtocolException e) {
			throw new RuntimeException("ClientProtocolException while executing scan results request for scan id <" + scanId + ">", e);
		} catch (final IOException e) {
			throw new RuntimeException("IOException while executing scan results request for scan id <" + scanId + ">", e);
		} catch (final URISyntaxException e) {
			throw new RuntimeException("URISyntaxException while executing scan results request for scan id <" + scanId + ">", e);
		}		
		
		return httpResponse;
	}
	
	private byte[] extractScanResultsData(final HttpResponse httpResponse) {
		logger.info("extracting scan results data");
		
		final HttpEntity httpEntity = httpResponse.getEntity();
		
		final byte[] scanResultsData;
		
		try {
			scanResultsData = EntityUtils.toByteArray(httpEntity);
		} catch (final IOException e) {
			throw new RuntimeException("IOException extract scan results data",
					e);
		}
		
		return scanResultsData;
	}
	
	private void validateResponse(final HttpResponse httpResponse, final int expectedStatusCode) {
		logger.info("validating response is <" + expectedStatusCode + ">");
		
		final StatusLine statusLine = httpResponse.getStatusLine();
		
		if (statusLine == null) {
			throw new RuntimeException("unexpected null status while validating response");
		}
		
		final int statusCode = statusLine.getStatusCode();
		
		if (expectedStatusCode != statusCode) {
			throw new RuntimeException("received unexpected status code <" + statusCode + "> while validating response");
		}
	}

}
