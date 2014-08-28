package me.automationdomination.plugins.webinspect.service;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebInspectServiceImpl implements WebInspectService {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final HttpClient httpClient;
	private final String webInspectUrl;
	
	public WebInspectServiceImpl(final String webInspectUrl) {
		this.httpClient = HttpClientBuilder.create()
				.setDefaultRequestConfig(RequestConfig.custom()
						.setSocketTimeout(900000)
						.setConnectTimeout(900000)
						.setConnectionRequestTimeout(900000).build()).build();
		this.webInspectUrl = webInspectUrl;
	}

	@Override
	public List<String> retrieveSettings() {
		final HttpResponse httpResponse;
		
		try {
			httpResponse = httpClient.execute(new HttpGet(webInspectUrl + "/settings"));
		} catch (final ClientProtocolException e) {
			throw new RuntimeException("ClientProtocolException while retrieving webinspect settings", e);
		} catch (final IOException e) {
			throw new RuntimeException("IOException while retrieving webinspect settings", e);
		}
		
		
		
		final StatusLine statusLine = httpResponse.getStatusLine();
		
		if (statusLine == null) {
			throw new RuntimeException("unexpected null status while retrieving webinspect settings");
		}
		
		
		
		final int statusCode = statusLine.getStatusCode();
		
		if (200 != statusCode) {
			throw new RuntimeException("received unexpected status code <" + statusCode + "> while retrieving webinspect settings");
		}
		
		
		final HttpEntity httpEntity = httpResponse.getEntity();
		final String settingsJson;
		
		try {
			settingsJson = EntityUtils.toString(httpEntity);
		} catch (final ParseException e) {
			throw new RuntimeException("exception parsing webinspect settings response json", e);
		} catch (IOException e) {
			throw new RuntimeException("IOException parsing webinspect settings response json", e);
		}
		
		
		final List<String> settings;
		
		try {
			settings = objectMapper.readValue(settingsJson, new TypeReference<List<String>>(){});
		} catch (final JsonParseException e) {
			throw new RuntimeException("JsonParseException unmarshalling webinspect settings response json", e);
		} catch (final JsonMappingException e) {
			throw new RuntimeException("JsonMappingException unmarshalling webinspect settings response json", e);
		} catch (final IOException e) {
			throw new RuntimeException("IOException unmarshalling webinspect settings response json", e);
		}
		
		return settings;		
	}
	
	@Override
	public void retrieveScanFile(final String settings, final String scanName) {
		final HttpResponse createWebInspectScanResponse = createWebInspectScan(settings, scanName);
		final String scanId = extractScanId(createWebInspectScanResponse);
		waitForStatusChangeComplete(scanId);
		retrieveAndWriteScanFile(scanId, scanName);
	}
	
	private HttpResponse createWebInspectScan(final String settings, final String scanName) {
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
		
		
		
		final HttpResponse httpResponse;
		
		try {
			final HttpPost httpPost = new HttpPost(webInspectUrl);
			httpPost.setEntity(new StringEntity("settingsName=" + settings + "&overrides=" + overridesString, Consts.UTF_8));			
			httpResponse = httpClient.execute(httpPost);
		} catch (final ClientProtocolException e) {
			throw new RuntimeException("ClientProtocolException while creating webinspect scan", e);
		} catch (final IOException e) {
			throw new RuntimeException("IOException while creating webinspect scan", e);
		}
		
		
		
		final StatusLine statusLine = httpResponse.getStatusLine();
		
		if (statusLine == null) {
			throw new RuntimeException("unexpected null status while creating webinspect scan");
		}

		final int statusCode = statusLine.getStatusCode();
		
		if (201 != statusCode) {
			throw new RuntimeException("received unexpected status code <" + statusCode + "> while creating webinspect scan");
		}
		
		
		
		return httpResponse;
	}
	
	private String extractScanId(final HttpResponse httpResponse) {
		final HttpEntity httpEntity = httpResponse.getEntity();
		
		
		
		final String scanIdJson;
		
		try {
			scanIdJson = EntityUtils.toString(httpEntity);
		} catch (final ParseException e) {
			throw new RuntimeException("exception parsing webinspect scan id response json", e);
		} catch (IOException e) {
			throw new RuntimeException("IOException parsing webinspect scan id response json", e);
		}
		
		
		
		final Map<String, String> scanIdResponse;
		
		try {
			scanIdResponse = objectMapper.readValue(scanIdJson, new TypeReference<Map<String, String>>(){});
		} catch (final JsonParseException e) {
			throw new RuntimeException("JsonParseException unmarshalling webinspect scan id response json", e);
		} catch (final JsonMappingException e) {
			throw new RuntimeException("JsonMappingException unmarshalling webinspect scan id response json", e);
		} catch (final IOException e) {
			throw new RuntimeException("IOException unmarshalling webinspect scan id response json", e);
		}
		
		
		
		if (!scanIdResponse.containsKey("ScanId")) {
			throw new RuntimeException("ScanId missing from webinspect scan id response json");
		}
		
		
		
		final String scanId = scanIdResponse.get("ScanId");
		return scanId;
	}
	
	private void waitForStatusChangeComplete(final String scanId) {
		final URIBuilder uriBuilder;
		
		try {
			uriBuilder = new URIBuilder(webInspectUrl + "/" + scanId)
				.setParameter("WaitForStatusChange", "Complete");
		} catch (final URISyntaxException e) {
			throw new RuntimeException("URISyntaxException while waiting for scan <" + scanId + "> completion", e);
		}
		
		
		
		try {
			httpClient.execute(new HttpGet(uriBuilder.build()));
		} catch (final ClientProtocolException e) {
			throw new RuntimeException("ClientProtocolException while waiting for scan <" + scanId + "> completion", e);
		} catch (final IOException e) {
			throw new RuntimeException("IOException while waiting for scan <" + scanId + "> completion", e);
		} catch (final URISyntaxException e) {
			throw new RuntimeException("URISyntaxException while waiting for scan <" + scanId + "> completion", e);
		}
	}
	
	private void retrieveAndWriteScanFile(final String scanId, final String outputFile) {
		final HttpResponse httpResponse;
		
		try {
			final URIBuilder waitForStatusChangeBuilder = new URIBuilder(webInspectUrl + "/" + scanId + ".fpr");
			httpResponse = httpClient.execute(new HttpGet(waitForStatusChangeBuilder.build()));
		} catch (final ClientProtocolException e) {
			throw new RuntimeException("ClientProtocolException while retrieving scan <" + scanId + "> result file", e);
		} catch (final IOException e) {
			throw new RuntimeException("IOException while retrieving scan <" + scanId + "> result file", e);
		} catch (final URISyntaxException e) {
			throw new RuntimeException("URISyntaxException while retrieving scan <" + scanId + "> result file", e);
		}		
		
		
		
		final StatusLine statusLine = httpResponse.getStatusLine();
		
		if (statusLine == null) {
			throw new RuntimeException("unexpected null status while retrieving scan <" + scanId + "> result file");
		}
		
		
		
		final int statusCode = statusLine.getStatusCode();
		
		if (200 != statusCode) {
			throw new RuntimeException("received unexpected status code <" + statusCode + "> while retrieving scan <" + scanId + "> result file");
		}
		
		
		
		final HttpEntity httpEntity = httpResponse.getEntity();
		
		try {
			int length = EntityUtils.toByteArray(httpEntity).length;
			System.out.println(length + " bytes!");
		} catch (final IOException e) {
			throw new RuntimeException("IOException reading scan <" + scanId + "> result file");
		}
	}

}
