package com.suvo.app;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;

public class Handler {

    private final S3Client s3Client;

    public Handler() {
        s3Client = DependencyFactory.s3Client();
    }

    public void sendRequest() {
        String bucket = "bucket" + System.currentTimeMillis();
        String key = "key";

        //createBucket(s3Client, bucket);
        getBucketList(s3Client);

/*
        System.out.println("Uploading object...");

        s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(key)
                        .build(),
                RequestBody.fromString("Testing with the {sdk-java}"));

        System.out.println("Upload complete");
        System.out.printf("%n");
*/

        //cleanUp(s3Client, bucket, key);

        System.out.println("Closing the connection to {S3}");
        s3Client.close();
        System.out.println("Connection closed");
        System.out.println("Exiting...");
    }

    public static void createBucket(S3Client s3Client, String bucketName) {
        try {
            s3Client.createBucket(CreateBucketRequest
                    .builder()
                    .bucket(bucketName)
                    .build());
            System.out.println("Creating bucket: " + bucketName);
            s3Client.waiter().waitUntilBucketExists(HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build());
            System.out.println(bucketName + " is ready.");
            System.out.printf("%n");
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void cleanUp(S3Client s3Client, String bucketName, String keyName) {
        System.out.println("Cleaning up...");
        try {
            System.out.println("Deleting object: " + keyName);
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(bucketName).key(keyName).build();
            s3Client.deleteObject(deleteObjectRequest);
            System.out.println(keyName + " has been deleted.");
            System.out.println("Deleting bucket: " + bucketName);
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
            s3Client.deleteBucket(deleteBucketRequest);
            System.out.println(bucketName + " has been deleted.");
            System.out.printf("%n");
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Cleanup complete");
        System.out.printf("%n");
    }

    public List<S3Object> getBucketList(S3Client s3Client){
        List<S3Object> bucketContent = new ArrayList<>();
        try {
            System.out.println("Getting bucket List!!!!");
            ListBucketsResponse bucketList = s3Client.listBuckets();

            List<Bucket> buckets = bucketList.buckets();
            System.out.println("Bucket Size: "+buckets.size());

            for (Bucket bucket:buckets) {
                System.out.println("Bucket Name: "+bucket.name());
                ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                        .bucket(bucket.name()).prefix("new").build();

                ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);

                bucketContent = listObjectsV2Response.contents();
                System.out.println("Number of objects in bucket: "+bucketContent.stream().count());

                //bucketContent.stream().forEach(System.out::println);
                for (S3Object obj:bucketContent) {
                    System.out.println(obj.key());
                }
            }
        }catch (S3Exception e){
            e.printStackTrace();
        }
        return bucketContent;
    }
}
