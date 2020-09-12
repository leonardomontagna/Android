package com.example.ripeto.progettoandroid;

public class User {
    String id;
    String email;

    public User(String id, String email) {
        this.id = id;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return email;
    }

    public void setName(String email) {
        this.email = email;
    }
}

