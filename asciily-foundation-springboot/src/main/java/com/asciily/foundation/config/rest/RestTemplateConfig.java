/**
 * Copyright [2015-2017]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.asciily.foundation.config.rest;

import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * @author xiaosw<xiaosw@msn.cn>
 * @since 2017年5月3日
 */
@Configuration
public class RestTemplateConfig {

	@Value("${rest.read.timeout}")
	private int readTimeOut = 2000;

	@Value("${rest.connect.timeout}")
	private int connectionTimeOut = 2000;

	@Bean
	public SimpleClientHttpRequestFactory httpClientFactory() {
		SimpleClientHttpRequestFactory httpClientFactory = new SimpleClientHttpRequestFactory();
		httpClientFactory.setReadTimeout(readTimeOut);
		httpClientFactory.setConnectTimeout(connectionTimeOut);
		return httpClientFactory;
	}

	@Bean
	public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

			public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				return true;
			}
		}).build();

		httpClientBuilder.setSSLContext(sslContext);
		HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
		SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslSocketFactory).build();
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connectionManager.setMaxTotal(200);
		connectionManager.setDefaultMaxPerRoute(100);
		httpClientBuilder.setConnectionManager(connectionManager);
		CloseableHttpClient client = httpClientBuilder.build();

		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(client);
		clientHttpRequestFactory.setReadTimeout(readTimeOut);
		clientHttpRequestFactory.setConnectTimeout(connectionTimeOut);
		clientHttpRequestFactory.setConnectionRequestTimeout(connectionTimeOut);
		return clientHttpRequestFactory;
	}

	@Bean
	public RestTemplate restTemplate() throws Exception {
		try {
			RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
			for (HttpMessageConverter messageConvert : restTemplate.getMessageConverters()) {
				try {
					if (messageConvert instanceof AbstractHttpMessageConverter) {
						((AbstractHttpMessageConverter) messageConvert).setDefaultCharset(Charset.forName("UTF-8"));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return restTemplate;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BeanInitializationException(e.getMessage());
		}
	}
}
