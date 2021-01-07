package com.example.healthcare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;




public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText e1,e2;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    String verification;
    SharedPreferences sp;
    Uri photoUri;
    LoginButton loginButton;
    CallbackManager callbackManager;
    String cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner=findViewById(R.id.country_code);
        spinner.setOnItemSelectedListener(this);

        e1=(EditText)findViewById(R.id.et1);
        e2=(EditText)findViewById(R.id.et2);

        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verification=s;
                Toast.makeText(getApplicationContext(), "Code sent to the number", Toast.LENGTH_SHORT).show();
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(this, gso);



        signInButton=(SignInButton)findViewById(R.id.mygooglebutton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });

        callbackManager=CallbackManager.Factory.create();


    }



    public void send_sms(View v)
    {
        if(e1.getText().toString().equals("")){
            e1.setError("Enter mobile number");
            e1.requestFocus();
            return;
        }
        String number =cc + e1.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(number,60, TimeUnit.SECONDS,this,mCallback);
    }

    public void signInWithPhone(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), "User signed in successfully", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(MainActivity.this,Main2Activity.class);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(myIntent);
                    finish();
                }
                else if(task.getException() instanceof FirebaseAuthUserCollisionException)
                {
                    Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Wrong code, Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void verify(View v)
    {
        if(e2.getText().toString().equals(""))
        {
            e2.setError("Enter received OTP");
            e2.requestFocus();
            return;
        }

        User user=new User(7,"user");

        SessionManagement sessionManagement=new SessionManagement(MainActivity.this);
        sessionManagement.saveSession(user);


        String input_code=e2.getText().toString();
        if(verification!=null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification,input_code);
            signInWithPhone(credential);
        }

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            User use=new User(1,"user");

                            SessionManagement sessionManagement=new SessionManagement(MainActivity.this);
                            sessionManagement.saveSession(use);

                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Sign in Successful", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(),Main2Activity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//    public void fbLogin(View view)
//    {
//
//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_photos", "email", "public_profile", "user_posts"));
//        LoginManager.getInstance().registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>()
//                {
//                    @Override
//                    public void onSuccess(LoginResult loginResult)
//                    {
//
//                        User user=new User(7,"user");
//
//                        SessionManagement sessionManagement=new SessionManagement(MainActivity.this);
//                        sessionManagement.saveSession(user);
//
//                        Toast.makeText(MainActivity.this, "Sign in Successful", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(MainActivity.this,Main2Activity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//
//                    @Override
//                    public void onCancel()
//                    {
//                        // App code
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception)
//                    {
//                        // App code
//                    }
//                });
//    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cc=parent.getSelectedItem().toString();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
