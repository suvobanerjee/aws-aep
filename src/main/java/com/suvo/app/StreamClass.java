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


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class StreamClass {

    public static void main(String args[]){
        String s3FileURL = "https://aepbatch.s3.eu-north-1.amazonaws.com/new/SBProgramEnrollmentSchema_aepbatch11.json";
        String bucketName = "aepbatch";
        String key = "new/SBProgramEnrollmentSchema_aepbatch11.json";
        Region region = Region.EU_NORTH_1;
        String targetApiURL = "https://platform.adobe.io/data/foundation/import/batches/01K1AYJWWMQPV54ZP3G9NZQDFM/datasets/68629fedce7d0f2b5ab3cabd/files/"+key;

        try(S3Client s3Client = S3Client.builder().httpClientBuilder(ApacheHttpClient.builder()).build();
            CloseableHttpClient httpClient = HttpClients.createDefault()
        ){

            System.out.println("Requesting S3 object: " + key + " from bucket: " + bucketName);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();

            try(ResponseInputStream<GetObjectResponse> s3InputStream = s3Client.getObject(getObjectRequest)){
/*
                File localFile = new File("E:\\dwn\\"+key);
                FileOutputStream fos = new FileOutputStream(localFile);
                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = s3InputStream.read(buffer)) != -1){
                    fos.write(buffer,0,bytesRead);
                }

                s3InputStream.close();
                fos.close();
                System.out.println("File download done");*/

                /*HttpEntity entity = MultipartEntityBuilder.create()
                        .addBinaryBody("file",s3InputStream, ContentType.APPLICATION_OCTET_STREAM,key)
                        .addTextBody("source","s3")
                        .build();*/

                //System.out.println("Request Entity: "+EntityUtils.toString(entity));
                HttpEntity requestEntity = new InputStreamEntity(s3InputStream,ContentType.APPLICATION_OCTET_STREAM);
                HttpPut httpPut = new HttpPut(targetApiURL);
                httpPut.setEntity(requestEntity);
                httpPut.setHeader("Authorization","eyJhbGciOiJSUzI1NiIsIng1dSI6Imltc19uYTEta2V5LWF0LTEuY2VyIiwia2lkIjoiaW1zX25hMS1rZXktYXQtMSIsIml0dCI6ImF0In0.eyJpZCI6IjE3NTM3NzMzMzYxNjVfNTJlMDU2YWMtYWRlNy00ODY1LWFkNWEtNmIyNDdkNWRkYjlkX3V3MiIsIm9yZyI6Ijg1NkY1QkRFNThDMTU4QTUwQTQ5NUQ1MEBBZG9iZU9yZyIsInR5cGUiOiJhY2Nlc3NfdG9rZW4iLCJjbGllbnRfaWQiOiIxOWU5OGNiYmQzZDI0NDkxYjk0NzQ1YTdlMDI5YTY1YyIsInVzZXJfaWQiOiI4NzA4MUQzRjY2RTdFQTBGMEE0OTVGRjNAdGVjaGFjY3QuYWRvYmUuY29tIiwiYXMiOiJpbXMtbmExIiwiYWFfaWQiOiI4NzA4MUQzRjY2RTdFQTBGMEE0OTVGRjNAdGVjaGFjY3QuYWRvYmUuY29tIiwiY3RwIjozLCJtb2kiOiI2ZDMyOWUyYiIsImV4cGlyZXNfaW4iOiI4NjQwMDAwMCIsInNjb3BlIjoib3BlbmlkLHNlc3Npb24sQWRvYmVJRCxyZWFkX29yZ2FuaXphdGlvbnMsYWRkaXRpb25hbF9pbmZvLnByb2plY3RlZFByb2R1Y3RDb250ZXh0IiwiY3JlYXRlZF9hdCI6IjE3NTM3NzMzMzYxNjUifQ.g5s9ciiYAGBwS_zbo7IlnEQZk_BWNFyiHoi571EUBsb6uYvy3XeywfWudEBCUD8tRg6vvxDYG9ZgRqvAhiernEWmQm8oZ77II6yixVgjBn7GDx4rqj2j7JLHdl5jUxGHlrOhsqB8_9JXbgqAxNFo5cMnKzX6YHJW_ssDYAt7F0OGppd3ILMX75hS_lNBk1gVLAKUzrkt4ubb2O55BIXiib5-LcvmdYz1A_qOVn1tTBnPkMREYeI7uqYIEOWeAjOpWr3PcO3sKP19sZ8u1KhC8NserX09wEI7yGoPngkFj6SK7PaqQnNWNsiF7oDLSYtuiL_nkgtHfPoRj9Ygcks3sw");
                httpPut.setHeader("x-api-key","19e98cbbd3d24491b94745a7e029a65c");
                httpPut.setHeader("x-gw-ims-org-id","856F5BDE58C158A50A495D50@AdobeOrg");
                httpPut.setHeader("x-sandbox-name","training-ucp");
                httpPut.setHeader("Content-Type","application/octet-stream");

                System.out.println("Streaming file to API: " + targetApiURL);

                httpClient.execute(httpPut,response ->{
                    System.out.println("----------------------------------------");
                    System.out.println("API Response Status: " + response.getCode());
                    String responseBody = EntityUtils.toString(response.getEntity());
                    System.out.println("-------------------------------------");
                    System.out.println(responseBody);
                    EntityUtils.consume(response.getEntity());
                    return null;
                });
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
