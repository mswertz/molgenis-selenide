package org.molgenis.selenide.test;

import static java.util.Arrays.asList;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.molgenis.data.rest.client.MolgenisClient;
import org.molgenis.util.GsonHttpMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config
{
	@Value("${test.baseurl}")
	private String baseURL;

	@Value("${test.uid}")
	private String uid;

	@Value("${test.pwd}")
	private String pwd;
	
	@Autowired
	GsonHttpMessageConverter converter;

	@Bean
	public RestTemplate restTemplate()
	{
		HttpClient client = HttpClientBuilder.create().disableCookieManagement().build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
		RestTemplate template = new RestTemplate(requestFactory);
		template.setMessageConverters(asList(converter));
		return template;
	}

	@Bean
	public MolgenisClient restClient()
	{
		String apiURL = String.format("%s/api/v1", baseURL);
		MolgenisClient molgenisClient = new MolgenisClient(restTemplate(), apiURL);
		molgenisClient.login(uid, pwd);
		return molgenisClient;
	}
}
