package com.example.ripeto.progettoandroid;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;


public class RequestNetAsync extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... args) {

        try {
            Log.d("RequestAsybc", "sono in backgrund");
            return sendPost(args[0], args[1]);// + json;
        } catch (Exception e) {
            return "";
        }
    }

    protected static String sendPost(String urlLink, String param) throws Exception {
        //Create the connection
        try {
            String parameters = param;

            URL url = new URL(urlLink);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(20000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            //connection.connect();
            //Send the request with the parameters
            Log.d("URL", String.valueOf(url));
            Log.d("Connection", String.valueOf(connection));
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            Log.d("SendPost", parameters);
            writer.write((parameters));
            writer.flush();
            writer.close();
            os.close();

            //Create the output
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                //Toast.makeText(context, "HTTP OK", Toast.LENGTH_SHORT).show();
                Log.d("responseCode", "OK");
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Log.d("Reader", "creato il buffer reader");
                StringBuffer sb = new StringBuffer("");
                String line = "";
                while ((line = in.readLine()) != null) {
                    Log.d("Line", line);
                    sb.append(line);
                    break;
                }
                in.close();
                Log.d("sendPost", "result: " + sb.toString());
                return sb.toString();
            }
            return "";

        } catch (IOException e) {
            // writing exception to log
            e.printStackTrace();
        }
        return "Errore";
    }
}