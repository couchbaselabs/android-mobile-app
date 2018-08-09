package com.couchbase.couchbase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences sharedPreferences = getSharedPreferences("ClusterName", Context.MODE_PRIVATE);
        if (savedInstanceState == null) {
            if(sharedPreferences.getString("Cluster", null) != null){
                Intent fragmentManager = new Intent(Login.this, FragmentManager.class);
                startActivity(fragmentManager);
            }
            else{
                getSupportFragmentManager().beginTransaction().replace(R.id.login_frame, new LoginHome()).commit();
            }
        }
    }
}