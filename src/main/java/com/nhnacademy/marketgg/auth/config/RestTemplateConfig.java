package com.nhnacademy.marketgg.auth.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.Objects;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Value("${gg.client-certificate.resource-name}")
    private String clientCertificateResName;

    @Value("${gg.client-certificate.password}")
    private String clientCertificatePassword;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, KeyManagementException {

        KeyStore clientStore = KeyStore.getInstance("PKCS12");

        clientStore.load(new FileInputStream(ResourceUtils.getFile(
                                 Objects.requireNonNull(this.getClass().getResource(clientCertificateResName)))),
                         clientCertificatePassword.toCharArray());

        SSLContext sslContext = SSLContexts.custom()
                                           .setProtocol("TLS")
                                           .loadKeyMaterial(clientStore, clientCertificatePassword.toCharArray())
                                           .loadTrustMaterial(new TrustSelfSignedStrategy())
                                           .build();

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        CloseableHttpClient httpClient = HttpClients.custom()
                                                    .setSSLSocketFactory(socketFactory)
                                                    .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return builder.setReadTimeout(Duration.ofSeconds(5L))
                      .setConnectTimeout(Duration.ofSeconds(3L))
                      .requestFactory(() -> factory)
                      .build();
    }

}
