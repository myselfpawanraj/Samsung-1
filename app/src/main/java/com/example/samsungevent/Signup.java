package com.example.samsungevent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signup extends AppCompatActivity {
    EditText name1,email1,pass1;
    Button signup;
    ClientAPI clientAPI = Utils.getClientAPI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        name1=findViewById(R.id.input_name1);
        email1=findViewById(R.id.input_email1);
        pass1=findViewById(R.id.input_password1);
        signup=findViewById(R.id.btn_signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=email1.getText().toString();
                String pass=pass1.getText().toString();
                String name=name1.getText().toString();
                 Log.e("de1",name);
                 Log.e("de2",email);
//                Map<String, String> params = new HashMap<>();
//                Map<String, String> params2 = new HashMap<>();
//                Map<String, String> params3 = new HashMap<>();
//                params.put("name",name);
//                params2.put("email",email);
//                params3.put("pass",pass);
 //           Call<ResponseClient2> call = clientAPI.signup("application/json",params,params2,params3);
                JsonObject jsonObject=new JsonObject();
                jsonObject.addProperty("name",name);
                jsonObject.addProperty("email",email);
                jsonObject.addProperty("pass",pass);
                clientAPI.signup(jsonObject).enqueue(new Callback<ResponseClient2>() {
                    @Override
                    public void onResponse(Call<ResponseClient2> call, Response<ResponseClient2> response) {
                        if(response.isSuccessful()){

                            Toast.makeText(Signup.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                            Log.e("de4",response.body().getSignup());
//                            Intent i=new Intent(Signup.this,Homepage.class);
//                            startActivity(i);
                        }
                        else{
                            Log.e("de3",response.message().toString());
                            Toast.makeText(Signup.this, response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseClient2> call, Throwable t) {
                        Toast.makeText(Signup.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                });
            }
        });
    }

    public void func(View v){
        Intent i=new Intent(Signup.this,MainActivity.class);
        startActivity(i);
    }
}