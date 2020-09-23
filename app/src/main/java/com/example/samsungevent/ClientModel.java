package com.example.samsungevent;

import java.io.Serializable;

public class ClientModel implements Serializable {

    String content_type,name,email,pass;

    public ClientModel(String content_type, String name, String email, String pass) {
        this.content_type = content_type;
        this.name = name;
        this.email = email;
        this.pass = pass;
    }

    public String getContent_type() {
        return content_type;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
