package com.example.samsungevent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    TextView signup;
    EditText email1,password1;
    String email,password;
    Button login;
    ClientAPI clientAPI = Utils.getClientAPI();
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        email1=(EditText)findViewById(R.id.input_email1);
        password1=(EditText)findViewById(R.id.input_password1);
        signup=findViewById(R.id.link_login);
        login=(Button)findViewById(R.id.btn_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email=email1.getText().toString();
                password=password1.getText().toString();
                if(email.isEmpty()){
                    Toast.makeText(MainActivity.this, "Email is Empty", Toast.LENGTH_SHORT).show();
                }
                else if(password.isEmpty()){
                    Toast.makeText(MainActivity.this, "Password is Empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    JsonObject jsonObject=new JsonObject();
                    jsonObject.addProperty("email",email);
                    jsonObject.addProperty("pass",password);
                     clientAPI.login(jsonObject).enqueue(new Callback<ResponseClient>() {
                         @Override
                         public void onResponse(Call<ResponseClient> call, Response<ResponseClient> response) {
                                if(response.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                    Intent i1=new Intent(MainActivity.this,Homepage.class);
                                    startActivity(i1);
                                    finish();


                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("isLoggedIn",true);
                                    editor.apply();
                                }
                                else{
                                    Toast.makeText(MainActivity.this, response.message().toString(), Toast.LENGTH_SHORT).show();
                                }
                         }

                         @Override
                         public void onFailure(Call<ResponseClient> call, Throwable t) {
                             Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                         }
                     });
                }

            }
        });

    }





    public void onClick(View v){
        Intent i=new Intent(MainActivity.this,Signup.class);
        startActivity(i);
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false);

        if(isLoggedIn){
            Intent i=new Intent(MainActivity.this,Homepage.class);
            startActivity(i);
            finish();
        }
    }
}