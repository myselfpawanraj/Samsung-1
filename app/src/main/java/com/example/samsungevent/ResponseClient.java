package com.example.samsungevent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseClient {

    @SerializedName("signIn")
    @Expose
    private boolean signin;

    @SerializedName("name")
    @Expose
    private String name;

    public ResponseClient(boolean signin, String name) {
        this.signin = signin;
        this.name = name;
    }

    public boolean isSignin() {
        return signin;
    }

    public void setSignin(boolean signin) {
        this.signin = signin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
