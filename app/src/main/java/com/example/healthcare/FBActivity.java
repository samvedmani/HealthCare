package com.example.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class FBActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    TextView txtname,txtEmail;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_b);
        txtname=findViewById(R.id.profile_name);
        txtEmail=findViewById(R.id.profile_email);
        circleImageView=findViewById(R.id.profile_pic);
        button=findViewById(R.id.logoutFB);
        checkLoginStatus();
    }

    AccessTokenTracker tokenTracker =new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken==null)
            {
                txtname.setText("");
                txtEmail.setText("");
                Toast.makeText(FBActivity.this, "User Logged out", Toast.LENGTH_SHORT).show();
            }
            else
                loaduserprofile(currentAccessToken);
        }
    };

    void loaduserprofile(AccessToken newAccessToken)
    {
        GraphRequest request=GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");

                    String image_url="https://graph.facebook.com/"+id+"/picture?type=normal";
                    txtEmail.setText(email);
                    txtname.setText(first_name+" "+last_name);

                    RequestOptions requestOptions=new RequestOptions();
                    requestOptions.dontAnimate();

                    Glide.with(FBActivity.this).load(image_url).into(circleImageView);

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields","first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void checkLoginStatus()
    {
        if(AccessToken.getCurrentAccessToken()!=null)
        {
            loaduserprofile(AccessToken.getCurrentAccessToken());
        }
    }

    public void FBlogout(View view) {

        SessionManagement sessionManagement=new SessionManagement(FBActivity.this);
        sessionManagement.removeSession();

        Intent intent=new Intent(FBActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(FBActivity.this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();
        LoginManager.getInstance().logOut();
    }
}
