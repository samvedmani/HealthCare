package com.example.healthcare;

import android.content.Intent;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class AppIntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Hey!","Welcome to HealthCare Planner",R.drawable.health, getResources().getColor(R.color.blue)));
        addSlide(AppIntroFragment.newInstance("What is it?","Store your medical records and use Covid-19 self-tracker",R.drawable.health, getResources().getColor(R.color.blue)));
        addSlide(AppIntroFragment.newInstance("What is it?","Keep reminders for medications and appointments and track menstrual cycles",R.drawable.health, getResources().getColor(R.color.blue)));
        addSlide(AppIntroFragment.newInstance("What is it?","Optical Character Recognition for text",R.drawable.health, getResources().getColor(R.color.blue)));
        addSlide(AppIntroFragment.newInstance("Be Happy, Be Healthy","Please Login",R.drawable.health, getResources().getColor(R.color.blue)));

        showSkipButton(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkSession();
    }

    private void checkSession() {
        SessionManagement sessionManagement=new SessionManagement(AppIntroActivity.this);
        int userID = sessionManagement.getSession();

        if(userID!=-1){
            Intent myIntent = new Intent(AppIntroActivity.this,Main2Activity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(myIntent);
        } else{

        }
    }

    @Override
    public void onSkipPressed() {
        super.onSkipPressed();
        Intent intent=new Intent(AppIntroActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDonePressed() {
        super.onDonePressed();
        Intent intent=new Intent(AppIntroActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}


