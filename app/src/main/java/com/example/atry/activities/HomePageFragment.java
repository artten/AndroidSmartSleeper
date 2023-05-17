package com.example.atry.activities;

import static android.app.PendingIntent.FLAG_MUTABLE;
import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.atry.R;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomePageFragment extends Fragment {

    private TextView welcomeTextView;
    private TextView alarmText;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("TAG", "This is a debug log message.");
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);



        welcomeTextView = view.findViewById(R.id.textViewWelcome);
        alarmText = view.findViewById(R.id.alarmText);

        SharedPreferences sh = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String s1 = sh.getString("email", "");
        welcomeTextView.setText("Welcome " + s1);


        Executor executor = Executors.newSingleThreadExecutor();
        Button setAlarm = view.findViewById(R.id.setAlarmButton);

        CronetEngine.Builder myBuilder = new CronetEngine.Builder(getContext());
        CronetEngine cronetEngine = myBuilder.build();
        TextView time = view.findViewById(R.id.editTextTime);

        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                "http://"+getString(R.string.ip)+":5000/get_alarm?" +
                        "email=" +  s1,
                new MyUrlRequestCallback(), executor);

        UrlRequest request = requestBuilder.build();
        request.start();

        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                        "http://"+getString(R.string.ip)+"/set_alarm?" +
                                "email=" +  s1 +
                                "&wake_time=" + time.getText().toString(),
                        new MyUrlRequestCallback(), executor);

                UrlRequest request = requestBuilder.build();
                request.start();

            }
        });

//        Date dat = new Date();
//        Calendar cal_alarm = Calendar.getInstance();
//        cal_alarm.setTime(dat);
//        cal_alarm.set(Calendar.HOUR_OF_DAY, 10);
//        cal_alarm.set(Calendar.MINUTE, 5);
//        cal_alarm.set(Calendar.SECOND, 0);
//        setAlarm(getActivity(), cal_alarm);



        return view;
    }

    public void setAlarm(Context context, Calendar cal_alarm) {
        Intent intent = new Intent(getContext(), PlayMusic.class);
        intent.setAction("start");
        intent.putExtra("time", "10000"); // ToDo get time of wake
        PendingIntent pendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(
                    getContext().getApplicationContext(), 234324243, intent, FLAG_MUTABLE);
        } else {
            pendingIntent = PendingIntent.getBroadcast(
                    getContext().getApplicationContext(), 234324243, intent, PendingIntent.FLAG_ONE_SHOT);
        }
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + (3 * 1000), pendingIntent);

//        Intent myIntent = new Intent(getContext(), PlayMusic.class);
//        myIntent.setAction("stop");
//        PendingIntent pendingIntent2 = null;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            pendingIntent2 = PendingIntent.getBroadcast(
//                    getContext().getApplicationContext(), 234324243, myIntent, FLAG_MUTABLE);
//        } else {
//            pendingIntent2 = PendingIntent.getBroadcast(
//                    getContext().getApplicationContext(), 234324243, myIntent, PendingIntent.FLAG_ONE_SHOT);
//        }
//        AlarmManager alarmManager2 = (AlarmManager)tmp.split("[.]")[0] + tom getContext().getSystemService(ALARM_SERVICE);
//        alarmManager2.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
//                + (30 * 1000), pendingIntent2);
    }

    public class MyUrlRequestCallback extends UrlRequest.Callback {
        private static final String TAG = "MyUrlRequestCallback";
        private Context context = null;
        @Override
        public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) {
            Log.i(TAG, "onRedirectReceived method called.");
            // You should call the request.followRedirect() method to continue
            // processing the request.
            request.followRedirect();
        }

        @Override
        public void onResponseStarted(UrlRequest request, UrlResponseInfo info) {
            Log.i(TAG, "onResponseStarted method called.");
            // You should call the request.read() method before the request can be
            // further processed. The following instruction provides a ByteBuffer object
            // with a capacity of 102400 bytes for the read() method. The same buffer
            // with data is passed to the onReadCompleted() method.
            request.read(ByteBuffer.allocateDirect(102400));
        }

        @Override
        public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) {
            Log.i(TAG, "onReadCompleted method called.");
            // You should keep reading the request until there's no more data.
            byteBuffer.clear();
            request.read(byteBuffer);
            String res = StandardCharsets.UTF_8.decode(byteBuffer).toString();
            Log.d("tag", res);
            String tmp = res.split("[,]", 0)[0];
            Log.d(TAG, "onReadCompleted: " + tmp);
            if (res.contains(":")){

                alarmText.setText("Your timer was set to be at " +
                        tmp.split("[.]")[0]);
                int time_too_wake = Integer.parseInt(tmp.split("[.]")[1]);
                Log.d(TAG, "onReadCompleted: " + time_too_wake);
                int hour =  Integer.parseInt(tmp.split("[.]")[0].split("[:]")[0]);
                int minute =  Integer.parseInt(tmp.split("[.]")[0].split("[:]")[1]);
                int seconds =  Integer.parseInt(tmp.split("[.]")[0].split("[:]")[2]);
                Date dat = new Date();
                Calendar cal_alarm = Calendar.getInstance();
                cal_alarm.setTime(dat);
                if (hour > cal_alarm.get(Calendar.HOUR_OF_DAY)
                        && minute > cal_alarm.get(Calendar.MINUTE)
                        && seconds > cal_alarm.get(Calendar.SECOND)) {
                    cal_alarm.add(Calendar.HOUR_OF_DAY, 24);
                }
                cal_alarm.add(Calendar.SECOND, -time_too_wake);
                cal_alarm.set(Calendar.HOUR_OF_DAY, hour);
                cal_alarm.set(Calendar.MINUTE, minute);
                cal_alarm.set(Calendar.SECOND, seconds);
                setAlarm(getActivity(), cal_alarm);
            }
            if (res.contains("ok")){
                Intent intent = new Intent(getContext(), HomePageActivity.class);
                startActivity(intent);
            }
            



        }

        @Override
        public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
            Log.i(TAG, "onSucceeded method called.");
        }

        @Override
        public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
            // The request has failed. If possible, handle the error.
            Log.e(TAG, "The request failed.", error);
        }

    }

}