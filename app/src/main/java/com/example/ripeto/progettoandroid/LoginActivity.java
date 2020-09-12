package com.example.ripeto.progettoandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import static com.example.ripeto.progettoandroid.extraFunctions.emailIsValid;
import static com.example.ripeto.progettoandroid.extraFunctions.passwordIsValid;


public class LoginActivity extends AppCompatActivity {
    public String strr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("RipeTO");

        final EditText email = (EditText) findViewById(R.id.username);
        final EditText psw = (EditText) findViewById(R.id.password);
        Button login = (Button) findViewById(R.id.accedi_btn);
        Button visualizza = (Button) findViewById(R.id.visualizza_btn);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString();
                String Password = psw.getText().toString();

                System.out.println(Email);
                if(emailIsValid(Email)&&passwordIsValid(Password)){

                    try {
                        String parameters = URLEncoder.encode("email","UTF-8")+ "=" + Email + "&"+
                                URLEncoder.encode("psw","UTF-8")+ "=" + psw.getText().toString();
                        String stringSon = new RequestNetAsync().execute("http://10.0.2.2:8080/RipeTO/Gestore?action=login",parameters).get();
                          if(stringSon.equals("0")||stringSon.equals("1")){
                            //Create the user session
                            String parameter = URLEncoder.encode("email","UTF-8")+ "=" + Email;
                            try {
                                JSONArray jSONArray = new JSONArray(new RequestNetAsync().execute("http://10.0.2.2:8080/RipeTO/Gestore?action=getid", parameter).get());
                                for (int i = 0; i < jSONArray.length();i++) {
                                    System.out.println("id calcolo");

                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append(jSONArray.get(i).toString());
                                    strr = stringBuilder.toString();
                                }




                            }catch(JSONException e){
                                e.printStackTrace();
                            }


                            System.out.println(strr);
                            User user = new User(strr, Email);
                            SessionManagement sessionManagement = new SessionManagement(LoginActivity.this);
                            //Save the Session
                            sessionManagement.saveSession(user);
                            sessionManagement.saveEmailSession(user);





                            Intent intent = new Intent(getApplicationContext(),PrenotazioniActivity.class);
                            intent.putExtra("Ruolo",stringSon);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Benvenuto!"+Email, Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "E-mail o password errati", Toast.LENGTH_LONG).show();
                        }



                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Email o password non corretti", Toast.LENGTH_LONG).show();
                }
            }
        });

        visualizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),DisponibiliActivity.class);
                startActivity(intent);

            }
        });

    }

}
