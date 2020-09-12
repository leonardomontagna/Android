package com.example.ripeto.progettoandroid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.json.JSONObject;

import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONException;


public class DisponibiliActivity extends AppCompatActivity {
    ArrayAdapter disponibiliadapter;

    ArrayList listadisponibili=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disponibili);
        setTitle("Lezioni Disponibli");
        ListView disponibili = (ListView) findViewById(R.id.disponibili);

        disponibiliadapter = new ArrayAdapter(this, R.layout.disponibili,R.id.disp, this.listadisponibili);//disponibili dell'activity e disp di disponibili
        new TaskLezioni().execute();
        disponibili.setAdapter(disponibiliadapter);


    }



    private class TaskLezioni extends AsyncTask<Void, Void, Void> {
        ArrayList<String> list;

        protected void onPreExecute () {
            super.onPreExecute();
            list = new ArrayList<>();

        }

        protected Void doInBackground(Void... param1VarArgs) {
            InputStream is=null;
            String result = "";

            try {
                URL uRL = new URL("http://10.0.2.2:8080/RipeTO/Gestore?action=getLezioni");

                HttpURLConnection httpURLConnection=(HttpURLConnection)uRL.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line="";

                while ((line = bufferedReader.readLine())!=null) {
                    result+=line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                JSONArray jSONArray = new JSONArray(result);

                for (int i = 0; i < jSONArray.length();i++) {


                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(jSONArray.getJSONObject(i).getString("nome"));
                    stringBuilder.append(" - ");
                    stringBuilder.append(jSONArray.getJSONObject(i).getString("titolo"));
                    stringBuilder.append("\n");
                    stringBuilder.append(jSONArray.getJSONObject(i).getString("data"));
                    stringBuilder.append("  ");
                    stringBuilder.append(jSONArray.getJSONObject(i).getString("ora"));
                    String str = stringBuilder.toString();
                    this.list.add(str);

                }
            } catch (JSONException jSONException) {
                jSONException.printStackTrace();
            }
            return null;
        }



        protected void onPostExecute (Void result) {
            listadisponibili.addAll(list);
            disponibiliadapter.notifyDataSetChanged();

        }



    }

}





