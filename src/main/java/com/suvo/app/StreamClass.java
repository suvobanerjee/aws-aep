package com.suvo.app;



import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.List;
import java.util.Properties;

public class StreamClass {

    public static void main(String args[]){
        //String s3FileURL = "https://aepbatch.s3.eu-north-1.amazonaws.com/new/SBProgramEnrollmentSchema_aepbatch11.json";
        Util util = new Util();
        Handler handler = new Handler();
        Properties prop = util.prop();
        String bucketName = prop.getProperty("bucket");
        //String key = "new/SBProgramEnrollmentSchema_aepbatch41_42.json";

        //try(S3Client s3Client = S3Client.builder().httpClientBuilder(ApacheHttpClient.builder()).build();
        try(S3Client s3Client = DependencyFactory.s3Client();
            CloseableHttpClient httpClient = HttpClients.createDefault()){

            List<S3Object> bucketContent = handler.getBucketList(s3Client);
            if(bucketContent.isEmpty())
            {
                System.out.println("Bucket content is empty");
            }
            else{
                System.out.println("Bucket content size: "+bucketContent.size());
            }
            for (S3Object obj: bucketContent) {
                System.out.println("Inside bucket content");
                String key = obj.key();
                if(!key.equals("new/")) {
                    System.out.println("Requesting S3 object: " + key + " from bucket: " + bucketName);
                    GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();

                    String targetApiURL = "https://platform.adobe.io/data/foundation/import/batches/01K2F22PQBGXA2KW35R93RCNRV/datasets/68629fedce7d0f2b5ab3cabd/files/" + key;

                    try (ResponseInputStream<GetObjectResponse> s3InputStream = s3Client.getObject(getObjectRequest)) {

                        HttpEntity requestEntity = new InputStreamEntity(s3InputStream, ContentType.APPLICATION_OCTET_STREAM);
                        HttpPut httpPut = new HttpPut(targetApiURL);
                        httpPut.setEntity(requestEntity);
                        httpPut.setHeader("Authorization", prop.getProperty("authorization"));
                        httpPut.setHeader("x-api-key", prop.getProperty("key"));
                        httpPut.setHeader("x-gw-ims-org-id", prop.getProperty("org"));
                        httpPut.setHeader("x-sandbox-name", prop.getProperty("sandbox"));
                        httpPut.setHeader("Content-Type", "application/octet-stream");

                        System.out.println("Streaming file to API: " + targetApiURL);

                        httpClient.execute(httpPut, response -> {
                            System.out.println("----------------------------------------");
                            System.out.println("API Response Status: " + response.getCode());
                            String responseBody = EntityUtils.toString(response.getEntity());
                            System.out.println("-------------------------------------");
                            System.out.println(responseBody);
                            EntityUtils.consume(response.getEntity());
                            return null;
                        });
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
