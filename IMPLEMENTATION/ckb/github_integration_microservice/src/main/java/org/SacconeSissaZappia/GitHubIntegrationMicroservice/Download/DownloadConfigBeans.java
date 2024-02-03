package org.SacconeSissaZappia.GitHubIntegrationMicroservice.Download;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class DownloadConfigBeans {

    @Bean
    public HttpClient githubClient() {
        return HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }
}
