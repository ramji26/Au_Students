package com.au.students;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread t = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    check();
                }
            }
        };
        t.start();

    }

    public void check(){
        SharedPreferences sharedPref = Splash.this.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        String defaultValue = "";
        String id = sharedPref.getString("ID", defaultValue);
        String name = sharedPref.getString("Name", defaultValue);
        if (id.equals(defaultValue) && name.equals(defaultValue)) {
            intent = new Intent(Splash.this, Entries.class);
            startActivity(intent);
        } else {
            intent = new Intent(Splash.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();

    }
}
