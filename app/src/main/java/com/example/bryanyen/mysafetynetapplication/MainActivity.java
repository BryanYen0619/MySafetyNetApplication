package com.example.bryanyen.mysafetynetapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private GoogleApiClient mGoogleApiClient;
    // reCAPTCHA 功能key，請至 https://g.co/recaptcha/androidsignup 申請。
    private String mSiteKey = "6LdaNCUUAAAAAHv0MIdCiJDx3DtxP6Nb_nN5B6Xc";

    private ImageView mCheckImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCheckImageView = (ImageView) findViewById(R.id.imageView);
        mCheckImageView.setVisibility(View.INVISIBLE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();

        mGoogleApiClient.connect();

        Button button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reCAPTCHA();
            }
        });
    }

    // 2017/06/09 新開放支援android，以往只支援網站
    private void reCAPTCHA() {
        SafetyNet.SafetyNetApi.verifyWithRecaptcha(mGoogleApiClient, mSiteKey)
                .setResultCallback(new ResultCallback<SafetyNetApi.RecaptchaTokenResult>() {
                    @Override
                    public void onResult(@NonNull SafetyNetApi.RecaptchaTokenResult result) {
                        Status status = result.getStatus();

                        if ((status != null) && status.isSuccess()) {
                            // Indicates communication with reCAPTCHA service was
                            // successful. Use result.getTokenResult() to get the
                            // user response token if the user has completed
                            // the CAPTCHA.

                            if (!result.getTokenResult().isEmpty()) {
                                // User response token must be validated using the
                                // reCAPTCHA site verify API.
                                Log.d("MY_APP_TAG", "get Token:" + result.getTokenResult());
                                mCheckImageView.setVisibility(View.VISIBLE);
                            }
                        } else {

                            Log.e("MY_APP_TAG", "Error occurred " +
                                    "when communicating with the reCAPTCHA service.");

                            // Use status.getStatusCode() to determine the exact
                            // error code. Use this code in conjunction with the
                            // information in the "Handling communication errors"
                            // section of this document to take appropriate action
                            // in your app.
                        }
                    }
                });
    }

    private void mVeriftyApp() {
        SafetyNet.getClient(MainActivity.this)
                .enableVerifyApps()
                .addOnCompleteListener(new OnCompleteListener<SafetyNetApi.VerifyAppsUserResponse>() {

                    @Override
                    public void onComplete(@NonNull Task<SafetyNetApi.VerifyAppsUserResponse> task) {
                        if (task.isSuccessful()) {
                            SafetyNetApi.VerifyAppsUserResponse response = task.getResult();
                            if (response.isVerifyAppsEnabled()) {
                                Log.d("MY_APP_TAG", "The user gave consent " +
                                        "to enable the Verify Apps feature.");
                            } else {
                                Log.d("MY_APP_TAG", "The user didn't give consent " +
                                        "to enable the Verify Apps feature.");
                            }
                        } else {
                            Log.e("MY_APP_TAG", "A general error occurred.");
                        }
                    }
                });
    }
}
