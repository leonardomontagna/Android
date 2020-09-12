package com.example.ripeto.progettoandroid;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "session";
    String SESSION_KEY = "session_user";

    SharedPreferences sharedPreferences1;
    SharedPreferences.Editor editor1;
    String SHARED_PREF_EMAIL = "SharedPref";
    String EMAIL_SESSION = "session_email";

    public SessionManagement(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        sharedPreferences1 = context.getSharedPreferences(SHARED_PREF_EMAIL,Context.MODE_PRIVATE);
        editor1 = sharedPreferences1.edit();
    }

    public void saveSession(User user){
        //save session of user whenever user is logged in
        String id = user.getId();
        editor.putString(SESSION_KEY,id).commit();
    }

    public void saveEmailSession(User user){
        String email = user.getName();
        editor1.putString(EMAIL_SESSION,email).commit();
    }

    public String getSession(){
        //return user id whose session is saved
        return sharedPreferences.getString(SESSION_KEY, "");
    }

    public String getEmailSession(){
        return sharedPreferences1.getString(EMAIL_SESSION, "");
    }
    public String getNameSession(){ return sharedPreferences.getString(SESSION_KEY,"");
    }

    public void removeSession(){
        editor.putInt(SESSION_KEY,-1).commit();
    }

    public void removeEmailSession(){
        editor1.putString(EMAIL_SESSION,"");
    }

}