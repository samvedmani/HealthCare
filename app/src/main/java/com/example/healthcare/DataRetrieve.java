package com.example.healthcare;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DataRetrieve extends AppCompatActivity {
    TextView a,b,c,d,e,f,g;
    DatabaseReference reff;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_retrieve);

        a=(TextView)findViewById(R.id.editText6);
        b=(TextView)findViewById((R.id.editText7));
        c=(TextView)findViewById(R.id.editText10);
        d=(TextView)findViewById(R.id.editText8);
        e=(TextView)findViewById(R.id.editText9);
        f=(TextView)findViewById(R.id.editText5);
        g=(TextView)findViewById(R.id.editTextOther);

        firebaseAuth=FirebaseAuth.getInstance();
    }

    public void fnShow(View view) {

        FirebaseUser user = firebaseAuth.getCurrentUser();
        reff= FirebaseDatabase.getInstance().getReference().child("Member").child(user.getUid());
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String age = dataSnapshot.child("age").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();
                    String addr = dataSnapshot.child("address").getValue().toString();
                    String phone = dataSnapshot.child("phone").getValue().toString();
                    String other = dataSnapshot.child("other").getValue().toString();
                    String other1 = dataSnapshot.child("other1").getValue().toString();

                    a.setText(name);
                    b.setText(age);
                    c.setText(gender);
                    d.setText(addr);
                    e.setText(phone);
                    f.setText(other);
                    g.setText(other1);
                }
                else{
                    Toast.makeText(DataRetrieve.this, "Not Available. Enter Data.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DataRetrieve.this, "Nothing to show", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
