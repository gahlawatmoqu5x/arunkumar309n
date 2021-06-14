package com.example.project.heartbeat.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.security.Security;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


/**
 * Created by balaji on 08/03/16.
 */
public class DatabaseUploader extends AsyncTask<File, Void,Void> {

    /**
     * In the calling function invoke in this way
     *  File database = getApplicationContext().getDatabasePath("MyAccDB.db");

     // Check if the database exists
     if (database.exists()) {
     ConnectivityManager connMgr = (ConnectivityManager)
     getSystemService(Context.CONNECTIVITY_SERVICE);
     NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
     if (networkInfo != null && networkInfo.isConnected()) {
     dbUploader.execute(database);
     }
     }
     else
     {
     System.out.println("No database");
     }

     */

    protected Void doInBackground(File... urls) {
    uploadDataBase(urls[0]);
        return null;
    }

     public void trustManager()
     {
         TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
             public X509Certificate[] getAcceptedIssuers() {
                 return null;
             }

             @Override
             public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                 // Not implemented
             }

             @Override
             public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                 // Not implemented
             }
         } };

         try {
             SSLContext sc = SSLContext.getInstance("TLS");

             sc.init(null, trustAllCerts, new java.security.SecureRandom());

             HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
         } catch (KeyManagementException e) {
             e.printStackTrace();
         } catch (NoSuchAlgorithmException e) {
             e.printStackTrace();
         }
     }

    public void uploadDataBase(File dbFile)
    {
        Log.v("1", "Db path is: " + dbFile);
        try
        {
            trustManager();
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary =  "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1*1024*1024;
            FileInputStream fileInputStream = new FileInputStream(dbFile );
            URL url = new URL("https://impact.asu.edu/Appenstance/UploadToServerGPS.php");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            // Allow Inputs &amp; Outputs.
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Set HTTP method to POST.
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

            DataOutputStream outputStream = new DataOutputStream( connection.getOutputStream() );
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            //outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0)
            {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();
            Log.v("1", "Server response code: " + serverResponseCode);
            Log.v("1", "Server response Message: " + serverResponseMessage);

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            //Exception handling
        }


    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


}
