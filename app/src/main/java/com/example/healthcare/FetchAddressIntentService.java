package com.example.healthcare;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FetchAddressIntentService extends IntentService {

    private ResultReceiver resultReceiver;

    public FetchAddressIntentService(){
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent!=null){
            String errorMsg="";
            resultReceiver=intent.getParcelableExtra(Constants.RECEIVER);
            Location location=intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
            if(location==null){
                return;
            }
            Geocoder geocoder=new Geocoder(this, Locale.getDefault());
            List<Address> addresses=null;
            try{
                addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);


            } catch (Exception e) {
                errorMsg=e.getMessage();
            }
            if(addresses==null || addresses.isEmpty()){
                deliverResultToReceiver(Constants.FAILURE_RESULT,errorMsg);
            }else{
                String addr=addresses.get(0).getAddressLine(0);
                String locality= addresses.get(0).getLocality();
                String t=addresses.get(0).getSubLocality();
                String state= addresses.get(0).getAdminArea();
                String country= addresses.get(0).getCountryName();
                String postalCode =addresses.get(0).getPostalCode();
                String address=t+", "+locality+", "+state+", "+country+", "+postalCode;

//                Address address = addresses.get(0);
//                ArrayList<String> addressFragments =new ArrayList<>();
//                for(int i=0;i<=address.getMaxAddressLineIndex();i++){
//                    addressFragments.add(address.getAddressLine(i));
//                }

                deliverResultToReceiver(Constants.SUCCESS_RESULT, TextUtils.join(Objects.requireNonNull(System.getProperty("line.separator")), Collections.singleton(address)));
            }
        }

    }
    private void deliverResultToReceiver(int resultCode,String addressMessage){
        Bundle bundle=new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY,addressMessage);
        resultReceiver.send(resultCode,bundle);

    }
}
