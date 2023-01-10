package com.or2go.or2gopartner;

import static com.or2go.core.Or2goConstValues.OR2GO_LOGIN_STATUS_FAILED;
import static com.or2go.core.Or2goConstValues.OR2GO_LOGIN_STATUS_SUCCESS;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    AppEnv gAppEnv;
    Context mContext;
    EditText textViewVendorId, textViewid, textViewpass;
    Button buttonlogin;
    String mVendorId;
    String mStoreId;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gAppEnv = (AppEnv) getApplicationContext();
        mContext = this;

        textViewVendorId = (EditText) findViewById(R.id.edvendid);
        textViewid = (EditText) findViewById(R.id.textViewID);
        textViewpass = (EditText) findViewById(R.id.textViewPassWord);
        buttonlogin = (Button) findViewById(R.id.buttonlogin);

        if (gAppEnv.getEnvStatus() == false) {
            System.out.println("Initializing global environment ");
            gAppEnv.InitGeniposEnv();
        }

        mVendorId = gAppEnv.gAppSettings.getVendorId();
        mStoreId = gAppEnv.gAppSettings.getStoreId();
        System.out.println("Login Activity mobile= "+mStoreId);
        if (!mVendorId.isEmpty()) textViewVendorId.setText(mVendorId);
        if (!mStoreId.isEmpty()) textViewid.setText(mStoreId);

        buttonlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
//                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
        });
    }

    private void doLogin() {
        String vendid = textViewVendorId.getText().toString();
        if(vendid.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please set valid SPID.", Toast.LENGTH_LONG).show();
            return;
        }
        String storeid = textViewid.getText().toString();
        if(storeid.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter valid store Id", Toast.LENGTH_LONG).show();
            return;
        }
        String passwd = textViewpass.getText().toString();
        if(passwd.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter password:", Toast.LENGTH_LONG).show();
            return;
        }
        if(passwd.length() < 6 ) {
            textViewpass.setError("Password length is less than 6");
            return;
        }

        //final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.Theme_AppCompat_Dialog);
        progressDialog = new ProgressDialog(LoginActivity.this, R.style.Theme_Or2goProgressDialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        progressDialog.show();


        System.out.println("Or2Go Vendor Login ID="+"  StoreId="+mStoreId);
        gAppEnv.Or2goLogin(vendid, storeid, passwd);


        final Handler mProgressHandler = new Handler();
        mProgressHandler.postDelayed(new Runnable() {
            public void run() {

                System.out.println("Or2Go Login State="+gAppEnv.getOr2goLoginStatus());
                //if (gAppEnv.getOr2goLoginStatus() == OR2GO_LOGIN_STATUS_FAILED) {
                if (gAppEnv.isLoggedIn()){

                    //uLogin.setEnabled(true);
                    progressDialog.dismiss();

                    mProgressHandler.removeCallbacks(null);

                    System.out.println("Or2Go Login Done ...moving to Dashboard=");
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));

                }
                else if (gAppEnv.getOr2goLoginStatus() == OR2GO_LOGIN_STATUS_FAILED)
                {
                    mProgressHandler.removeCallbacks(null);
                    progressDialog.dismiss();

                    buttonlogin.setEnabled(true);
                    System.out.println("Or2Go Login Error");
                }
                else
                {
                    String apimsg;

                    if (gAppEnv.getOr2goLoginStatus() == OR2GO_LOGIN_STATUS_SUCCESS)
                        apimsg = " Initializing  Vendor List ....";
                    else
                        apimsg = " Logging in ....";

                    progressDialog.setMessage(apimsg);

                    mProgressHandler.postDelayed(this, 1000);
                    progressDialog.dismiss();
                }
            }
        }, 1000);
    }
}