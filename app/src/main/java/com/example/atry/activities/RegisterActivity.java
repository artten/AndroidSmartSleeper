package com.example.atry.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.atry.R;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button register = findViewById(R.id.register);
        EditText password = findViewById(R.id.editTextTextPassword);
        EditText email = findViewById(R.id.editTextTextEmail);
        EditText birthday = findViewById(R.id.editTextBithday);
        EditText gender = findViewById(R.id.editTextGender);
        EditText height = findViewById(R.id.editTextNumberHeight);
        EditText weight = findViewById(R.id.editTextNumberWeight);
        CronetEngine.Builder myBuilder = new CronetEngine.Builder(this);
        CronetEngine cronetEngine = myBuilder.build();
        final String transform_gender;
        if (gender.getText().equals("female")) {
            transform_gender = "0";
        } else {
            transform_gender = "1";
        }


        Executor executor = Executors.newSingleThreadExecutor();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                        "http://192.168.1.206:5000/register?" +
                                "email=" +  email.getText() +
                                "&birthday=" + birthday.getText() +
                                "&gender=" + transform_gender +
                                "&height=" + height.getText() +
                                "&weight=" + weight.getText() +
                                "&password=" + password.getText(),
                        new MyUrlRequestCallback(), executor);

                UrlRequest request = requestBuilder.build();
                request.start();
            }
        });
    }

}

