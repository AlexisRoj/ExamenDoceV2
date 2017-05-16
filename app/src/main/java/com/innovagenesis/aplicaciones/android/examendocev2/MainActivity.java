package com.innovagenesis.aplicaciones.android.examendocev2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements FacebookCallback<LoginResult> {

    public static final String TAG = MainActivity.class.getSimpleName();

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private TextView textLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);

        /*loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));*/

        loginButton.setReadPermissions("email");


        textLogin = (TextView)findViewById(R.id.txtview_email);


        loginButton.registerCallback(callbackManager,this);

        ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if (currentProfile!= null){
                    //textLogin.setText(currentProfile.getName());
                }
            }
        };

        profileTracker.startTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }

    @Override
    public void onSuccess(LoginResult loginResult) {

        // App code
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("LoginActivity", response.toString());

                        // Application code
                        try {
                            String email = object.getString("email");
                            textLogin.setText(email);
                            //String birthday = object.getString("birthday"); // 01/31/1980 format

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
        Bundle parameters = new Bundle();
        //parameters.putString("fields", "id,name,email,gender,birthday");
        parameters.putString("fields", "email");
        request.setParameters(parameters);
        request.executeAsync();



        Log.i(TAG, "onSuccess: " + loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {
        Log.i(TAG, "onCancel: Se ha cancelado la operación de inicio de sesión");
    }

    @Override
    public void onError(FacebookException error) {
        Log.i(TAG, "onError: " + error.getMessage());
    }
}
