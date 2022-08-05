package com.nhnacademy.marketgg.auth.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.time.Duration;
import java.util.Optional;
import javax.net.ssl.SSLContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * REST 형식에 맞는 HTTP 통신에 사용 가능한 템플릿 설정 클래스입니다.
 */
@Slf4j
@Configuration
public class RestTemplateConfig {

    /**
     * 인증서를 등록합니다.
     *
     * @param keystoreType     - 암호화 방식
     * @param keystorePath     - 인증서 위치
     * @param keystorePassword - 인증서 비밀번호
     * @param protocol         - 인증 방식
     * @return 인증서의 정보를 가지고 있다.
     * @author 이제훈
     */
    @Bean
    CloseableHttpClient httpClient(final @Value("${gg.keystore.type}") String keystoreType,
                                   final @Value("${gg.keystore.path}") String keystorePath,
                                   final @Value("${gg.keystore.password}") String keystorePassword,
                                   final @Value("${gg.protocol}") String protocol) {

        SSLContext sslContext;

        try {

            KeyStore clientStore = KeyStore.getInstance(keystoreType);
            Resource resource = new ClassPathResource(keystorePath);
            clientStore.load(resource.getInputStream(), keystorePassword.toCharArray());

            sslContext = SSLContexts.custom()
                                    .setProtocol(protocol)
                                    .loadKeyMaterial(clientStore, keystorePassword.toCharArray())
                                    .loadTrustMaterial(new TrustSelfSignedStrategy())
                                    .build();

        } catch (GeneralSecurityException gse) {
            log.error("", gse);
            throw new IllegalArgumentException(gse);
        } catch (IOException ie) {
            log.error("", ie);
            throw new UncheckedIOException(ie);
        }

        SSLConnectionSocketFactory socketFactory
            = new SSLConnectionSocketFactory(Optional.ofNullable(sslContext).orElseThrow());

        return HttpClients.custom()
                          .setSSLSocketFactory(socketFactory)
                          .setMaxConnTotal(100)
                          .setMaxConnPerRoute(5)
                          .build();
    }

    /**
     * 인증서 관련 정보를 바탕으로 RestTemplate 의 통신에 관련된 설정을 합니다.
     *
     * @param httpClient - 인증서의 정보
     * @return RestTemplate 의 설정 정보
     * @author 이제훈
     */
    @Bean
    HttpComponentsClientHttpRequestFactory requestFactory(final HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        requestFactory.setReadTimeout(5_000);
        requestFactory.setConnectTimeout(3_000);
        requestFactory.setHttpClient(httpClient);

        return requestFactory;
    }

    /**
     * 인증서가 등록된 RestTemplate 을 빈으로 등록합니다.
     *
     * @param requestFactory RestTemplate 생성에 관한 정보를 포함하고 있습니다.
     * @return RestTemplate
     * @author 이제훈
     */
    @Bean(name = "clientCertificateAuthenticationRestTemplate")
    public RestTemplate restTemplate(final HttpComponentsClientHttpRequestFactory requestFactory) {
        return new RestTemplate(requestFactory);
    }

    /**
     * REST 형식에 맞는 HTTP 통신에 사용 가능한 템플릿입니다.
     *
     * @param builder - RestTemplate 생성 시 필요한 설정을 포함하여 RestTemplate 빈 생성 가능한 객체
     * @return RestTemplate
     * @author 이제훈
     */
    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder builder) {
        return builder.setReadTimeout(Duration.ofSeconds(5L))
                      .setConnectTimeout(Duration.ofSeconds(3L))
                      .build();
    }

}
