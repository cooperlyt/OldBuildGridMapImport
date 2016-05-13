package com.cooper.house;

import com.scoopit.weedfs.client.AssignParams;
import com.scoopit.weedfs.client.Assignation;
import com.scoopit.weedfs.client.WeedFSClient;
import com.scoopit.weedfs.client.WeedFSClientBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;


import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.URLConnection;

/**
 * Created by cooper on 4/11/16.
 */
public class PutPersonImg {

    private static final String SRC_DIR = "/root/Documents/personImg";

    private static final String MASTER_ADDRESS = "http://192.168.1.8:9333";

    private static final String TT_SERVER_ADDRESS= "http://192.168.1.8:1978/";

    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public static void main(String[] args) throws IOException {




        File root = new File(SRC_DIR);
        File[] files = root.listFiles();
        for(File f: files) {
            if (f.isFile()) {
                String key = getFileNameNoEx(f.getName()).trim();
                key = key.substring(1);

                try {
                    if (get(TT_SERVER_ADDRESS + key) != 200) {


                        WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(new URL(MASTER_ADDRESS)).build();
                        Assignation a = client.assign(new AssignParams());
                        client.write(a.weedFSFile, a.location, f);


                        System.out.println("put person " + key + ":" + post(TT_SERVER_ADDRESS + key, a.weedFSFile.fid));
                    } else {

                        System.out.println("dump person: " + key);
                    }
                }catch (Exception e){
                    System.out.println("error:" + key);
                }
            }
        }


    }


    public static int  get(String url){
        HttpClient client = HttpClientBuilder.create().build();

        HttpGet put = new HttpGet(url);

        try {
            HttpResponse httpResponse = client.execute(put);
             return httpResponse.getStatusLine().getStatusCode() ;
        } catch (IOException e) {
            e.printStackTrace();
            return 404;
        }

    }

    public static int post(String url,String data){
                HttpClient client = HttpClientBuilder.create().build();

                HttpPut put = new HttpPut(url);
               try {
                        StringEntity s = new StringEntity(data,"UTF-8");
                        s.setContentEncoding("UTF-8");
                        s.setContentType("text/html");
                    put.setEntity(s);

                       HttpResponse res = client.execute(put);
                   return res.getStatusLine().getStatusCode();

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                //return response;
            }


}
