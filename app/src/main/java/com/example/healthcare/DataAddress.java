package com.example.healthcare;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DataAddress extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private  static final int REQUEST_CODE_LOCATION_PERMISSION=1;
    private TextView textAddress;
    private ProgressBar progressBar;
    private ResultReceiver resultReceiver;
    RadioButton yes,no;
    String gender;
    private EditText et_other, et_name, et_age, et_addr, et_phone, et_gender, editText1;
    ProgressDialog pd;
    Button b5;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    CheckBox cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8;
    FirebaseDatabase firebaseDatabase;
    Member member;
    AutoCompleteTextView editText;
    ArrayAdapter<String> adapter;
    String a;
    LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_address);

        resultReceiver=new AddressResultReceiver(new Handler());

        String[] conditions=getResources().getStringArray(R.array.Conditions);
        editText = findViewById(R.id.actv);
        editText1=findViewById(R.id.editTextAddress);
        adapter= new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, conditions);
        editText.setAdapter(adapter);

        Spinner spinner=findViewById(R.id.gender);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.gender,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



        et_other = (EditText) findViewById(R.id.editText5);
        et_name = (EditText) findViewById(R.id.editText6);
        et_age = (EditText) findViewById(R.id.editText7);
        //et_addr = (EditText) findViewById(R.id.editText8);
        et_phone = (EditText) findViewById(R.id.editText9);
        //et_gender = (EditText) findViewById(R.id.editText10);
        b5 = (Button) findViewById(R.id.button5);
        member=new Member();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Member");
        firebaseAuth=FirebaseAuth.getInstance();

        progressBar=findViewById(R.id.progressBar);

        yes=findViewById(R.id.radioyes);
        no=findViewById(R.id.radiono);

        findViewById(R.id.buttonCurrentLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DataAddress.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);
                } else {
                    getCurrentLocation();
                }

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length>0) {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            } else{
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation(){
        progressBar.setVisibility(View.VISIBLE);

        locationRequest=new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());



        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                Toast.makeText(DataAddress.this, "Received Address ",
                        Toast.LENGTH_LONG).show();
                Log.d("location settings",locationSettingsResponse.toString());
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(DataAddress.this,
                                REQUEST_CODE_LOCATION_PERMISSION);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });


        LocationServices.getFusedLocationProviderClient(DataAddress.this).requestLocationUpdates(locationRequest,new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(DataAddress.this).removeLocationUpdates(this);
                if(locationResult != null && locationResult.getLocations().size() > 0) {
                    int latestLocationIndex = locationResult.getLocations().size()-1;
                    double latitude=locationResult.getLocations().get(latestLocationIndex).getLatitude();
                    double longitude=locationResult.getLocations().get(latestLocationIndex).getLongitude();
                    Location location=new Location("providerNA");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    fetchAddressFromLatLong(location);

                }else{
                    progressBar.setVisibility(View.GONE);
                }

            }
        }, Looper.getMainLooper());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


            if(resultCode==RESULT_OK){

                Toast.makeText(this, "GPS enabled", Toast.LENGTH_SHORT).show();
                Log.d("result ok",data.toString());

            }else if(resultCode==RESULT_CANCELED){

                Toast.makeText(this, "Unable to open GPS",
                        Toast.LENGTH_SHORT).show();
                Log.d("result cancelled",data.toString());
            }

    }

    private void fetchAddressFromLatLong(Location location) {
        Intent intent=new Intent(this,FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER,resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,location);
        startService(intent);
    }

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler){
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if(resultCode==Constants.SUCCESS_RESULT){
                editText1.setText(resultData.getString(Constants.RESULT_DATA_KEY));
            } else{
                Toast.makeText(DataAddress.this, resultData.getString(Constants.RESULT_DATA_KEY), Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    public void fnRegister(View view) {

        if(et_name.getText().toString().equals(""))
        {
            et_name.setError("Enter your name");
            et_name.requestFocus();
            return;
        }

        if(et_age.getText().toString().equals(""))
        {
            et_age.setError("Enter your age");
            et_age.requestFocus();
            return;
        }

//        if(et_gender.getText().toString().equals(""))
//        {
//            et_gender.setError("Enter your gender");
//            et_gender.requestFocus();
//            return;
//        }

        if(editText1.getText().toString().equals(""))
        {
            editText1.setError("Enter your address");
            editText1.requestFocus();
            return;
        }

        if(et_phone.getText().toString().equals(""))
        {
            et_phone.setError("Enter your number");
            et_phone.requestFocus();
            return;
        }

        int age=Integer.parseInt(et_age.getText().toString().trim());
        Long phone=Long.parseLong((et_phone.getText().toString().trim()));

        String m1=yes.getText().toString();
        String m2=no.getText().toString();

        member.setName(et_name.getText().toString().trim());
        member.setAddress(editText1.getText().toString().trim());
        member.setAge(age);
        member.setGender(gender);
        member.setPhone(phone);
        member.setOther(editText.getText().toString().trim());
        member.setOther1(et_other.getText().toString().trim());

        if(yes.isChecked()){
            member.setCovid(m1);
        } else {
            member.setCovid(m2);
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.child(user.getUid()).setValue(member);
        Toast.makeText(this, "Data inserted successfully", Toast.LENGTH_LONG).show();


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        gender=parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}