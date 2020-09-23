package com.example.samsungevent;


public class Utils
{
    private Utils(){}

    public static final String BaseUrl="https://dibasol.herokuapp.com/signup/";

    public static ClientAPI getClientAPI()
    {
        return RetrofitClient.getClient(BaseUrl).create(ClientAPI.class);
    }
}

