package com.example.healthcare.addmedicine;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.healthcare.Injection;
import com.example.healthcare.OCR;
import com.example.healthcare.R;
import com.example.healthcare.utils.ActivityUtils;

public class AddMedicineActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_TASK = 1;

    public static final String SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY";

    public static final String EXTRA_TASK_ID = "task_extra_id";
    public static final String EXTRA_TASK_NAME = "task_extra_name";

    private AddMedicinePresenter mAddMedicinePresenter;

    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        //Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        AddMedicineFragment addMedicineFragment = (AddMedicineFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        int medId = getIntent().getIntExtra(AddMedicineFragment.ARGUMENT_EDIT_MEDICINE_ID,0);
        String medName = getIntent().getStringExtra(AddMedicineFragment.ARGUMENT_EDIT_MEDICINE_NAME);

        setToolbarTitle(medName);


        if (addMedicineFragment == null) {
            addMedicineFragment = AddMedicineFragment.newInstance();

            if (getIntent().hasExtra(AddMedicineFragment.ARGUMENT_EDIT_MEDICINE_ID)) {
                Bundle bundle = new Bundle();
                bundle.putInt(AddMedicineFragment.ARGUMENT_EDIT_MEDICINE_ID, medId);
                addMedicineFragment.setArguments(bundle);
            }
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addMedicineFragment, R.id.contentFrame);
        }

        boolean shouldLoadDataFromRepo = true;
        // Prevent the presenter from loading data from the repository if this is a config change.
        if (savedInstanceState != null) {
            // Data might not have loaded when the config change happen, so we saved the state.
            shouldLoadDataFromRepo = savedInstanceState.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY);
        }

//        // Create the presenter
        mAddMedicinePresenter = new AddMedicinePresenter(
                medId,
                Injection.provideMedicineRepository(getApplicationContext()),
                addMedicineFragment,
                shouldLoadDataFromRepo);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("My Notification","My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }


    }

    public void setToolbarTitle(String medicineName) {
        if (medicineName == null) {
            mActionBar.setTitle(getString(R.string.new_medicine));
        } else {
            mActionBar.setTitle(medicineName);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY, mAddMedicinePresenter.isDataMissing());
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void goToOCR(View view) {
        Intent intent=new Intent(this,OCR.class);
        startActivity(intent);
    }
}