package com.example.healthcare;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MyAlarmManager extends AppCompatActivity implements View.OnClickListener {

    TimePicker timePicker;
    EditText editText;
    private int notificationId=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_manager);
        timePicker =findViewById(R.id.timePicker);
        editText = findViewById(R.id.edt);


        findViewById(R.id.setAlarm).setOnClickListener(this);
        findViewById(R.id.cancelAlarm).setOnClickListener(this);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("My Notification","My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

    }

@Override
public void onClick(View view) {
    EditText editText = findViewById(R.id.edt);
    TimePicker timePicker = findViewById(R.id.timePicker);


    Intent intent = new Intent(MyAlarmManager.this, MyAlarm.class);
    intent.putExtra("notificationId", notificationId);
    intent.putExtra("todo", editText.getText().toString());


    PendingIntent alarmIntent = PendingIntent.getBroadcast(MyAlarmManager.this, 0,
            intent, PendingIntent.FLAG_CANCEL_CURRENT);

    android.app.AlarmManager alarm = (android.app.AlarmManager) getSystemService(Context.ALARM_SERVICE);

    switch (view.getId()) {
        case R.id.setAlarm:
            int hour = timePicker.getCurrentHour();
            int minute = timePicker.getCurrentMinute();


            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, hour);
            startTime.set(Calendar.MINUTE, minute);
            startTime.set(Calendar.SECOND, 0);
            long alarmStartTime = startTime.getTimeInMillis();


            alarm.setAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent);

            Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
            break;

        case R.id.cancelAlarm:
            alarm.cancel(alarmIntent);
            Toast.makeText(this, "Cancelled.", Toast.LENGTH_SHORT).show();
            break;
    }

}



}
