package com.example.samsungevent;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseClient2 {
    @SerializedName("SignUp")
    @Expose
    private String signup;

    public ResponseClient2(String signup) {
        this.signup = signup;
    }

    public String getSignup() {
        return signup;
    }

    public void setSignup(String signup) {
        this.signup = signup;
    }
}
