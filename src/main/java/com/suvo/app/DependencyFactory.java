package com.suvo.app;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
public class DependencyFactory {
    private DependencyFactory() {}

    /**
     * @return an instance of S3Client
     */
    public static S3Client s3Client() {
        DefaultCredentialsProvider defaultCredentialsProvider = DefaultCredentialsProvider
                .builder()
                .profileName("suvo-dev-profile")
                .build();
        return S3Client.builder()
                .region(Region.EU_NORTH_1)
                .credentialsProvider(defaultCredentialsProvider)
                .httpClientBuilder(ApacheHttpClient.builder())
                .build();
    }
}
