package com.suvo.app;


import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

public class AEPOps {

    public String createBatch(String datasetId){
        Util utils = new Util();
        Properties prop = utils.prop();

        String batchId = "";
        String targetBatchURL = "https://platform.adobe.io/data/foundation/import/batches";
        System.out.println("Creating Batch: "+targetBatchURL);

        try(CloseableHttpClient httpClient = HttpClients.createDefault()){
            String requestBody = utils.createBatchBody(datasetId);
            System.out.println("Create Batch Body: "+requestBody);
            StringEntity entity = new StringEntity(requestBody, Consts.UTF_8);

            HttpPost post = new HttpPost(targetBatchURL);
            post.addHeader("Authorization", prop.getProperty("authorization"));
            post.addHeader("x-api-key", prop.getProperty("key"));
            post.addHeader("x-gw-ims-org-id", prop.getProperty("org"));
            post.addHeader("x-sandbox-name", prop.getProperty("sandbox"));
            post.addHeader("Content-Type", "application/json");
            post.setEntity(entity);

            HttpResponse response = httpClient.execute(post);
            System.out.println("--------------------------------");
            System.out.println("API Response Status Batch Id: " + response.getStatusLine().getStatusCode());
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("Response Body From Batch Id: " + responseBody);

            Object obj = JSONValue.parse(responseBody);
            JSONObject jsonObject = (JSONObject) obj;

            batchId = jsonObject.get("id").toString();
        }catch (IOException e){
            System.out.println("Exception in Create Batch!!!!!!!!!");
            e.printStackTrace();
        }
        return batchId;
    }

}
