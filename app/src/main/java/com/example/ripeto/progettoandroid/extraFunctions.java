package com.example.ripeto.progettoandroid;

import java.util.regex.Pattern;

public class extraFunctions {
    public static boolean emailIsValid(String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"; // email pattern
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pattern.matcher(email).matches();
    }

    public static boolean passwordIsValid(String password){
        String passwordRegex = "(.)*(\\d)(.)*"; //string has a number pattern
        Pattern pattern = Pattern.compile(passwordRegex);
        if(password == null)
            return false;
        if(password.length()<8)
            return false;
        return pattern.matcher(password).matches();
    }


}
