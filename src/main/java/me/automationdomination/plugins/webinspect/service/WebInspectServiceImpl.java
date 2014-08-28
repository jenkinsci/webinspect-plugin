package me.automationdomination.plugins.webinspect.service;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebInspectServiceImpl implements WebInspectService {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final HttpClient httpClient = HttpClientBuilder.create().build();
	private final String webInspectUrl;
	
	public WebInspectServiceImpl(final String webInspectUrl) {
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

}
