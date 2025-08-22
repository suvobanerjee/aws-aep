package com.suvo.app;

import org.json.simple.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Util {

    public Properties prop(){
        Properties prop = new Properties();
        try (InputStream ios = new FileInputStream("config.properties")){
            prop.load(ios);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }


    public String createBatchBody(String datasetId){
        JSONObject obj = new JSONObject();
        obj.put("datasetId",datasetId);

        JSONObject inputFormat = new JSONObject();
        inputFormat.put("format","json");

        obj.put("inputFormat",inputFormat);

        return obj.toJSONString();
    }
}
