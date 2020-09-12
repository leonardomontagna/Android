package com.example.ripeto.progettoandroid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PrenotazioniActivity extends AppCompatActivity {
    Spinner giornoSpinner;
    Spinner materieSpinner;
    Spinner oraSpinner;
    Spinner profSpinner;

    ArrayList<String> listMaterie = new ArrayList<>();
    ArrayList<String> listProf = new ArrayList<>();
    ArrayAdapter<String> materieAdapter;
    ArrayAdapter<String> profAdapter;

    String ruolo;


    private boolean checkSession() {
        SessionManagement sessionManagement = new SessionManagement(PrenotazioniActivity.this);
        String i = sessionManagement.getSession();
        String str = sessionManagement.getEmailSession();
        return (i != "" && str != "");
    }

    private String selectDay() {
        String str = "";
        if (giornoSpinner.getSelectedItem().toString().equals("Lunedi")) {
            str = "Lunedi";
        } else if (giornoSpinner.getSelectedItem().toString().equals("Martedi")) {
            str = "Martedi";
        } else if (giornoSpinner.getSelectedItem().toString().equals("Mercoledi")) {
            str = "Mercoledi";
        } else if (giornoSpinner.getSelectedItem().toString().equals("Giovedi")) {
            str = "Giovedi";
        } else if (giornoSpinner.getSelectedItem().toString().equals("Venerdi")) {
            str = "Venerdi";
        }
        return str;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prenotazioni);
        setTitle("Area Personale");
        new TaskMaterie().execute();
        new TaskProf().execute();
        Button v=(Button)findViewById(R.id.btn_visualizzapren);

        String[] time = new String[]{
                "dalle 15 alle 16",
                "dalle 16 alle 17",
                "dalle 17 alle 18",
                "dalle 18 alle 19"
        };

        String[] day = new String[]{
                "Lunedi",
                "Martedi",
                "Mercoledi",
                "Giovedi",
                "Venerdi"
        };


        Button prenota = (Button) findViewById(R.id.btn_prenota);

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if(extras.containsKey("Ruolo")) {
            ruolo = i.getStringExtra("Ruolo");
        }

        if(ruolo.equals("1")){
            findViewById(R.id.btn_visualizzapren).setVisibility(View.INVISIBLE);}




        materieSpinner = (Spinner)findViewById(R.id.materia);
        materieAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,this.listMaterie);
        materieSpinner.setAdapter((SpinnerAdapter)this.materieAdapter);

        profSpinner = (Spinner)findViewById(R.id.professore);
        profAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item, this.listProf);//pren è in prenotazione
        profSpinner.setAdapter((SpinnerAdapter)this.profAdapter);


        giornoSpinner = (Spinner)findViewById(R.id.giorno);
        ArrayAdapter arrayAdapter1 = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,day);
        arrayAdapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        giornoSpinner.setAdapter((SpinnerAdapter)arrayAdapter1);


        oraSpinner = (Spinner)findViewById(R.id.ora);
        ArrayAdapter arrayAdapter2 = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item,time);
        arrayAdapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        oraSpinner.setAdapter((SpinnerAdapter)arrayAdapter2);


        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Le mie prenotazioni", Toast.LENGTH_LONG).show();
                startActivity(new Intent(PrenotazioniActivity.this,StoricoActivity.class));

            }
        });

        prenota.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (checkSession() == true) {
                    SessionManagement sessionManagement = new SessionManagement(PrenotazioniActivity.this);
                    try {
                        Intent intent;
                        StringBuilder stringBuilder = new StringBuilder();

                        stringBuilder.append(URLEncoder.encode("email", "UTF-8"));
                        stringBuilder.append("=");
                        stringBuilder.append(sessionManagement.getEmailSession());
                        stringBuilder.append("&");
                        stringBuilder.append(URLEncoder.encode("nome_insegnate", "UTF-8"));
                        stringBuilder.append("=");
                        stringBuilder.append(PrenotazioniActivity.this.profSpinner.getSelectedItem().toString());
                        stringBuilder.append("&");
                        stringBuilder.append(URLEncoder.encode("nome_materia", "UTF-8"));
                        stringBuilder.append("=");
                        stringBuilder.append(PrenotazioniActivity.this.materieSpinner.getSelectedItem().toString());
                        stringBuilder.append("&");
                        stringBuilder.append(URLEncoder.encode("giorno", "UTF-8"));
                        stringBuilder.append("=");
                        stringBuilder.append(PrenotazioniActivity.this.giornoSpinner.getSelectedItem().toString());
                        stringBuilder.append("&");
                        stringBuilder.append(URLEncoder.encode("ora", "UTF-8"));
                        stringBuilder.append("=");
                        stringBuilder.append(oraSpinner.getSelectedItem().toString());
                        stringBuilder.append("&");
                        stringBuilder.append(URLEncoder.encode("ID", "UTF-8"));
                        stringBuilder.append("=");
                        stringBuilder.append(sessionManagement.getNameSession());
                        String str2 = stringBuilder.toString();

                        String str1 =new RequestNetAsync().execute("http://10.0.2.2:8080/RipeTO/Gestore?action=booklesson", str2 ).get();
                        if (str1.equals("1")) {

                            Toast.makeText(getApplicationContext(), "Prenotazione effettuata!", Toast.LENGTH_LONG).show();
                        } else if (str1.equals("0")) {
                            Toast.makeText(getApplicationContext(), "Hai prenotato troppo tardi!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Errore!", Toast.LENGTH_LONG).show();
                        }
                    } catch (UnsupportedEncodingException unsupportedEncodingException) {
                        unsupportedEncodingException.printStackTrace();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    } catch (ExecutionException executionException) {
                        executionException.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Devi effettuare il login per prenotare!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }




    public boolean onCreateOptionsMenu(Menu paramMenu) {
        getMenuInflater().inflate(R.menu.menu, paramMenu);
        if (checkSession()==true) {
            paramMenu.findItem(R.id.logout).setVisible(true);
            paramMenu.findItem(R.id.visualizza_btn).setVisible(true);
        } else {
            paramMenu.findItem(R.id.logout).setVisible(false);
            paramMenu.findItem(R.id.visualizza_btn).setVisible(false);
        }
        return super.onCreateOptionsMenu(paramMenu);
    }


    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        SessionManagement sessionManagement;
        switch (paramMenuItem.getItemId()) {
            default:
                return super.onOptionsItemSelected(paramMenuItem);

            case R.id.logout:
                Toast.makeText(getApplicationContext(), "Logout Effettutato!", Toast.LENGTH_LONG).show();

                sessionManagement = new SessionManagement(this);
                sessionManagement.removeEmailSession();
                sessionManagement.removeSession();
                moveToLogin();
                break;
            case R.id.visualizza_btn:
                moveToLezioni();
                break;
        }

        return false;
    }

    private void moveToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void moveToLezioni() {
        Intent intent = new Intent(this, DisponibiliActivity.class);
        startActivity(intent);
    }



    private class TaskProf extends AsyncTask<Void, Void, Void> {
        ArrayList<String> list;



        protected Void doInBackground(Void... param1VarArgs) {

            InputStream is=null;
            String result = "";
            try {
                URL uRL = new URL("http://10.0.2.2:8080/RipeTO/Gestore?action=getDocenti");

                HttpURLConnection httpURLConnection=(HttpURLConnection)uRL.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line="";

                while ((line = bufferedReader.readLine())!=null) {
                    result+=line;
                }
            } catch (MalformedURLException MalException) {
                MalException.printStackTrace();


            } catch (IOException iOException) {
                iOException.printStackTrace();

            }
            try {
                JSONArray jSONArray = new JSONArray(result);

                for (int i = 0; i < jSONArray.length();i++) {

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(jSONArray.getJSONObject(i).getString("nome"));
                    String str = stringBuilder.toString();
                    list.add(str);
                }
            } catch (JSONException jSONException) {
                jSONException.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void param1Void) {
            listProf.addAll(this.list);
            profAdapter.notifyDataSetChanged();
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.list = new ArrayList<>();
        }
    }



    private class TaskMaterie extends AsyncTask<Void, Void, Void> {
        ArrayList<String> list;



        protected Void doInBackground(Void... param1VarArgs) {
            InputStream is=null;
            String result = "";
            try {
                URL uRL = new URL("http://10.0.2.2:8080/RipeTO/Gestore?action=getmaterie");

                HttpURLConnection httpURLConnection=(HttpURLConnection)uRL.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line="";

                while ((line = bufferedReader.readLine())!=null) {
                    result+=line;
                }
            } catch (MalformedURLException MalException) {
                MalException.printStackTrace();


            } catch (IOException iOException) {
                iOException.printStackTrace();

            }
            try {
                JSONArray jSONArray = new JSONArray(result);

                for (int i = 0; i < jSONArray.length();i++) {
                    System.out.println(i);



                    JSONObject jSONObject = jSONArray.getJSONObject(i);

                    list.add(jSONObject.getString("titolo"));

                }
            } catch (JSONException jSONException) {
                jSONException.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void param1Void) {
            listMaterie.addAll(this.list);
            materieAdapter.notifyDataSetChanged();
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.list = new ArrayList<>();
        }
    }
}



