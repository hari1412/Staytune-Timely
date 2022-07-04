package com.buenatech.staytune.signinproviders;

import static com.buenatech.staytune.activities.MainActivity.PREF_KEY_FIRST_START;
import static com.buenatech.staytune.activities.MainActivity.REQUEST_CODE_INTRO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.buenatech.staytune.R;
import com.buenatech.staytune.activities.MainActivity;
import com.buenatech.staytune.activities.MainIntroActivity;
import com.buenatech.staytune.utils.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class FirstActivity extends AppCompatActivity {
    private static final String TAG = "GoogleSignInActivity";
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    RelativeLayout googles;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);




        // ini
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(FirstActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(PREF_KEY_FIRST_START, false)
                        .apply();
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(PREF_KEY_FIRST_START, true)
                        .apply();
                //User cancelled the intro so we'll finish this activity too.
                finish();
            }
        }


    }


    private void gotoProfile() {
        Intent intent = new Intent(FirstActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void gotosignuppage(View view) {
        Intent intent = new Intent(getApplicationContext(), EmailAndPasswordRegisterActivity.class);
        startActivity(intent);
    }

    public void gotosigninpage(View view) {
        Intent intent = new Intent(getApplicationContext(), EmailAndPasswordLoginActivity.class);
        startActivity(intent);
    }

}