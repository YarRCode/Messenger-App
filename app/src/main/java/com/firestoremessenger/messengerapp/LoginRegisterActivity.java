package com.firestoremessenger.messengerapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginRegisterActivity extends AppCompatActivity {

    private static final String TAG = "LoginRegisterActivity";
    int AUTHUI_REQUEST_CODE = 10023;

    IdpResponse response;

    FirebaseUser user;
    Intent intent, authUIntent;
    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        intent = new Intent(this, MainActivity.class);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(intent);
            this.finish();
        }
        init();
    }

    void init() {
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
                //new AuthUI.IdpConfig.PhoneBuilder().build(),
                //new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        authUIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
    }

    public void handleLoginRegister(View view){
        startActivityForResult(authUIntent, AUTHUI_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == AUTHUI_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                user = FirebaseAuth.getInstance().getCurrentUser();

                if(user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()) {
                    //New User
                    Toast.makeText(this, "Welcone New User", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Welcone Back", Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
                this.finish();

            } else {
                response = IdpResponse.fromResultIntent(data);
                if(response == null) {
                    Log.d(TAG, "the user has cancelled the sign request");
                } else {
                    Log.e(TAG,"Error:", response.getError());
                }
            }
        }
    }
}