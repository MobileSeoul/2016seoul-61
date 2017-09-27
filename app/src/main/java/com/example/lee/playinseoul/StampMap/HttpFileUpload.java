package com.example.lee.playinseoul.StampMap;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xyom on 2016-09-11.
 */
public class HttpFileUpload {
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";

    FileInputStream mFileInputStream;
    URL connectUrl;
    HttpURLConnection conn;

    String urlString;
    String fileName;

    DataOutputStream dos;


    public HttpFileUpload()
    {}
    public HttpFileUpload(String urlString, String fileName)
    {
        this.urlString=urlString;
        this.fileName=fileName;
    }

    public void initiate()
    {
        try {
            mFileInputStream = new FileInputStream(fileName);
            connectUrl = new URL(urlString);

            conn = (HttpURLConnection)connectUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            dos = new DataOutputStream(conn.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeProperty(String name,String data)
    {
        try {
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\""+name+"\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeUTF(data); // 내용 부분만 UTF로 전송해줘도 된당.
            dos.writeBytes(lineEnd);
            dos.writeBytes(lineEnd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFile()
    {
        try {
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName+"\"" + lineEnd);
            dos.writeBytes(lineEnd);
            int bytesAvailable = mFileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];
            int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
            // read image
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = mFileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void endTask()
    {
        try {
            mFileInputStream.close();
            dos.flush(); // finish upload...
            // get response
            InputStream is = conn.getInputStream();
            String str="";
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            while((str=br.readLine())!=null)
            {
                System.out.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
