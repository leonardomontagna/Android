package com.example.ripeto.progettoandroid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class StoricoActivity extends AppCompatActivity {
    ArrayAdapter arrayAdapter;

    RadioButton attive;

    RadioGroup rg;

    Button disdetta;


    RadioButton disdette;

    ArrayAdapter disdetteAdapter;

    Button effettuata;

    RadioButton effettuate;

    ArrayAdapter effettuateAdapter;

    ArrayList<Integer> idList = new ArrayList<>();

    ListView listView;

    ArrayList<String> listaDisdette = new ArrayList<>();

    ArrayList<String> listaEffettuate = new ArrayList<>();

    ArrayList<String> listaAttive = new ArrayList<>();

    private boolean checkSession() {
        SessionManagement sessionManagement = new SessionManagement(this);
        String i = sessionManagement.getSession();
        String str = sessionManagement.getEmailSession();
        return (i != ""&& str != "");
    }

    private void moveToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void moveToLezioni() {
        Intent intent = new Intent(this, DisponibiliActivity.class);
        startActivity(intent);
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




    public void getAttive(){
        System.out.println("ATTIVE");
        effettuata.setVisibility(View.VISIBLE);
        disdetta.setVisibility(View.VISIBLE);
        new TaskAttive().execute();
        listView.setAdapter((ListAdapter)this.arrayAdapter);
        listaAttive.toString();

    }
    public void getEffettuate(){
        System.out.println("EFFETTUATE");
        effettuata.setVisibility(View.GONE);
        disdetta.setVisibility(View.GONE);
        new TaskEffettuate().execute();
        listView.setAdapter((ListAdapter)this.effettuateAdapter);
        listaEffettuate.toString();

    }
    public void getDisdette(){
        System.out.println("DISDETTE");
        effettuata.setVisibility(View.GONE);
        disdetta.setVisibility(View.GONE);
        new TaskDisdette().execute();
        listView.setAdapter((ListAdapter)this.disdetteAdapter);
        listaDisdette.toString();

    }





    protected void onStart() {
        super.onStart();
        if (checkSession() == true) {
            SessionManagement sessionManagement = new SessionManagement(this);
        }
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storico);
        setTitle("Storico");

        rg=findViewById(R.id.rg);


        listView = (ListView)findViewById(R.id.storico);
        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE);

        arrayAdapter = new ArrayAdapter(this,  R.layout.storico,R.id.sto, this.listaAttive);
        listView.setAdapter((ListAdapter)this.arrayAdapter);
        effettuateAdapter = new ArrayAdapter(this, R.layout.storico,R.id.sto, this.listaEffettuate);
        listView.setAdapter((ListAdapter)this.effettuateAdapter);
        disdetteAdapter = new ArrayAdapter(this, R.layout.storico,R.id.sto, this.listaDisdette);
        listView.setAdapter((ListAdapter)this.disdetteAdapter);

        attive = (RadioButton)findViewById(R.id.rb_attive);
        effettuate = (RadioButton)findViewById(R.id.rb_effettuate);
        disdette = (RadioButton)findViewById(R.id.rb_disdette);

        effettuata = (Button)findViewById(R.id.btn_effettuata);
        disdetta = (Button)findViewById(R.id.btn_disdetta);


        attive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAttive();

            }
        });
        effettuate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEffettuate();

            }
        });
        disdette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDisdette();

            }
        });





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            String[] support;

            public void onItemClick(AdapterView<?> param1AdapterView, View param1View, int param1Int, long param1Long) {
                String str =param1AdapterView.getItemAtPosition(param1Int).toString().substring(0,2);//((TextView)param1View).getText().toString().substring(0, 2);
                System.out.println(str);
                StoricoActivity.this.idList.add(Integer.valueOf(Integer.parseInt(str)));

            }
        });


        effettuata.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                try {
                    if (!StoricoActivity.this.idList.isEmpty()) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(URLEncoder.encode("id", "UTF-8"));
                        stringBuilder.append("=");
                        stringBuilder.append(StoricoActivity.this.idList);
                        String str = stringBuilder.toString();
                        RequestNetAsync requestNetAsync = new RequestNetAsync();
                        str =new RequestNetAsync().execute("http://10.0.2.2:8080/RipeTO/Gestore?action=effettuaripetizioni", str ).get();

                        if (str.equals("0")) {
                            Toast.makeText(getApplicationContext(), "Errore!", Toast.LENGTH_LONG).show();
                            StoricoActivity.this.idList.clear();
                        } else if (str.equals("1")) {
                            Toast.makeText(getApplicationContext(), "Effettuato!", Toast.LENGTH_LONG).show();
                            StoricoActivity.this.idList.clear();
                        } else {
                            Toast.makeText(getApplicationContext(), "Qualcosa è andato storto!", Toast.LENGTH_LONG).show();
                            StoricoActivity.this.idList.clear();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Nessuna ripetizione selezionata!", Toast.LENGTH_LONG).show();
                    }
                } catch (UnsupportedEncodingException unsupportedEncodingException) {
                    unsupportedEncodingException.printStackTrace();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                } catch (ExecutionException executionException) {
                    executionException.printStackTrace();
                }
            }
        });




        disdetta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(URLEncoder.encode("id", "UTF-8"));
                    stringBuilder.append("=");
                    stringBuilder.append(StoricoActivity.this.idList);
                    String str = stringBuilder.toString();
                    RequestNetAsync requestNetAsync = new RequestNetAsync();
                    str =new RequestNetAsync().execute("http://10.0.2.2:8080/RipeTO/Gestore?action=disdiciripetizioni", str ).get();
                    if (str.equals("0")) {
                        Toast.makeText(getApplicationContext(), "Errore", Toast.LENGTH_LONG).show();
                    } else if (str.equals("1")) {
                        Toast.makeText(getApplicationContext(), "Disdetta", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Qualcosa è andato storto", Toast.LENGTH_LONG).show();
                    }
                } catch (UnsupportedEncodingException unsupportedEncodingException) {
                    unsupportedEncodingException.printStackTrace();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                } catch (ExecutionException executionException) {
                    executionException.printStackTrace();
                }
            }
        });
    }


    private class TaskDisdette extends AsyncTask<Void, Void, Void> {
        ArrayList<String> list;



        protected Void doInBackground(Void... param1VarArgs) {
            String str2 = "";
            String result = "";
            StoricoActivity.this.listaDisdette.clear();
            try {
                SessionManagement sessionManagement = new SessionManagement(StoricoActivity.this);



                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append("http://10.0.2.2:8080/RipeTO/Gestore?action=getripetizioni&ID=");
                stringBuilder.append(sessionManagement.getNameSession());
                str2=stringBuilder.toString();
                URL uRL = new URL(str2);
                HttpURLConnection httpURLConnection=(HttpURLConnection)uRL.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line="";
                while ((line = bufferedReader.readLine())!=null) {
                    result+=line;
                }
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
            try {
                JSONArray jSONArray = new JSONArray(result);

                for (int i = 0; i < jSONArray.length();i++) {


                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(jSONArray.getJSONObject(i).getInt("id"));
                    stringBuilder.append("\nDOCENTE: ");
                    stringBuilder.append(jSONArray.getJSONObject(i).getString("nome"));
                    stringBuilder.append("\nCORSO: ");
                    stringBuilder.append(jSONArray.getJSONObject(i).getString("titolo"));
                    stringBuilder.append("\nDATA: ");
                    stringBuilder.append(jSONArray.getJSONObject(i).getString("data"));
                    stringBuilder.append(" ");
                    stringBuilder.append(jSONArray.getJSONObject(i).getString("ora"));

                    stringBuilder.append(jSONArray.getJSONObject(i).getString("stato"));
                    String str = stringBuilder.toString();
                    if(jSONArray.getJSONObject(i).getString("stato").equals("disdetta"))
                        list.add(str);

                }
            } catch (JSONException jSONException) {
                jSONException.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void param1Void) {
            listaDisdette.addAll(this.list);
            disdetteAdapter.notifyDataSetChanged();
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.list = new ArrayList<>();
        }
    }


    private class TaskEffettuate extends AsyncTask<Void, Void, Void> {
        ArrayList<String> list;


        protected Void doInBackground(Void... param1VarArgs) {
            String str2 = "";
            String result = "";
            StoricoActivity.this.listaEffettuate.clear();
            try { SessionManagement sessionManagement = new SessionManagement(StoricoActivity.this);



                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append("http://10.0.2.2:8080/RipeTO/Gestore?action=getripetizioni&ID=");
                stringBuilder.append(sessionManagement.getNameSession());
                str2=stringBuilder.toString();
                URL uRL = new URL(str2);
                HttpURLConnection httpURLConnection=(HttpURLConnection)uRL.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line="";
                while ((line = bufferedReader.readLine())!=null) {
                    result+=line;
                }


            } catch (IOException iOException) {
                iOException.printStackTrace();

            }
            try {
                JSONArray jSONArray = new JSONArray(result);

                for (int i = 0; i < jSONArray.length();i++) {


                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(jSONArray.getJSONObject(i).getInt("id"));
                    stringBuilder.append("\nDOCENTE: ");
                    stringBuilder.append(jSONArray.getJSONObject(i).getString("nome"));
                    stringBuilder.append("\nCORSO: ");
                    stringBuilder.append(jSONArray.getJSONObject(i).getString("titolo"));
                    stringBuilder.append("\nDATA: ");
                    stringBuilder.append(jSONArray.getJSONObject(i).getString("data"));
                    stringBuilder.append(" ");
                    stringBuilder.append(jSONArray.getJSONObject(i).getString("ora"));

                    stringBuilder.append(jSONArray.getJSONObject(i).getString("stato"));
                    String str = stringBuilder.toString();
                    if(jSONArray.getJSONObject(i).getString("stato").equals("effettuata"))
                        list.add(str);

                }
            } catch (JSONException jSONException) {
                jSONException.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void param1Void) {
            listaEffettuate.addAll(this.list);
            effettuateAdapter.notifyDataSetChanged();
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.list = new ArrayList<>();
        }
    }
    private class TaskAttive extends AsyncTask<Void, Void, Void> {
        ArrayList<String> list;


        protected Void doInBackground(Void... param1VarArgs) {
            String str2 = "";
            String result = "";

            StoricoActivity.this.listaAttive.clear();

            try { SessionManagement sessionManagement = new SessionManagement(StoricoActivity.this);



                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append("http://10.0.2.2:8080/RipeTO/Gestore?action=getripetizioni&ID=");
                stringBuilder.append(sessionManagement.getNameSession());
                str2=stringBuilder.toString();
                URL uRL = new URL(str2);
                HttpURLConnection httpURLConnection=(HttpURLConnection)uRL.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line="";

                while ((line = bufferedReader.readLine())!=null) {
                    result+=line;

                }


            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
            try {
                JSONArray jSONArray = new JSONArray(result);

                System.out.println(jSONArray.length());

                for (int i = 0; i < jSONArray.length();i++) {


                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(jSONArray.getJSONObject(i).getInt("id"));
                    stringBuilder.append("\nDOCENTE: ");
                    stringBuilder.append(jSONArray.getJSONObject(i).getString("nome"));
                    stringBuilder.append("\nCORSO: ");
                    stringBuilder.append(jSONArray.getJSONObject(i).getString("titolo"));
                    stringBuilder.append("\nDATA: ");
                    stringBuilder.append(jSONArray.getJSONObject(i).getString("data"));
                    stringBuilder.append(" ");
                    stringBuilder.append(jSONArray.getJSONObject(i).getString("ora"));

                    String str = stringBuilder.toString();

                    if(jSONArray.getJSONObject(i).getString("stato").equals("attiva"))
                        list.add(str);

                }


            } catch (JSONException jSONException) {
                jSONException.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void param1Void) {
            listaAttive.addAll(this.list);
            arrayAdapter.notifyDataSetChanged();
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.list = new ArrayList<>();
        }
    }


}
