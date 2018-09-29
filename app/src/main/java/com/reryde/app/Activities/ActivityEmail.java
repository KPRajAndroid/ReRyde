package com.reryde.app.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hbb20.CountryCodePicker;
import com.reryde.app.Helper.CustomDialog;
import com.reryde.app.Helper.SharedHelper;
import com.reryde.app.Helper.URLHelper;
import com.reryde.app.R;
import com.reryde.app.RerydeApplication;
import com.reryde.app.Utils.MyTextView;
import com.reryde.app.Utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.reryde.app.RerydeApplication.trimMessage;

/**
 * Created by jayakumar on 31/01/17.
 */

public class ActivityEmail extends AppCompatActivity {

    ImageView backArrow;
    FloatingActionButton nextICON;
    EditText email,emailnew;
    MyTextView register, forgetPassword,emailtxt;
    LinearLayout lnrBegin;
    CountryCodePicker ccp;
    Context mContext;
    CustomDialog customDialog;
    Bundle extra;
    boolean  Register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        mContext=ActivityEmail.this;
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        email = (EditText)findViewById(R.id.enter_ur_mailID);
        emailnew= (EditText)findViewById(R.id.email);
        nextICON = (FloatingActionButton) findViewById(R.id.nextICON);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        register = (MyTextView) findViewById(R.id.register);
        forgetPassword = (MyTextView) findViewById(R.id.forgetPassword);
        lnrBegin = (LinearLayout) findViewById(R.id.lnrBegin);
        emailtxt = (MyTextView) findViewById(R.id.emailtxt);

        extra = getIntent().getExtras();
        if(extra != null) {
            //check whether register or login flow
            Register = extra.getBoolean("signup");
               if (Register) {
                   register.setVisibility(View.GONE);
                   emailnew.setVisibility(View.VISIBLE);
                   emailtxt.setVisibility(View.VISIBLE);
               }
        }else {
            emailtxt.setVisibility(View.GONE);
            emailnew.setVisibility(View.GONE);
            register.setVisibility(View.GONE);
        }

        nextICON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Register) {
                    if (email.getText().toString().length()==0){
                        Toast.makeText(mContext,"Please Enter a Mobile Number",Toast.LENGTH_SHORT).show();
                    }else if (email.getText().toString().length()>10){
                        Toast.makeText(mContext,"Please Enter a valid Mobile Number",Toast.LENGTH_SHORT).show();
                    }else if (emailnew.getText().toString().length()==0){
                        Toast.makeText(mContext,"Please Enter your Email",Toast.LENGTH_SHORT).show();
                    }else if (!isValidEmaillId(emailnew.getText().toString())){
                        Toast.makeText(mContext,"Please Enter a Valid Email",Toast.LENGTH_SHORT).show();
                    }else {
                        Utilities.hideKeyboard(ActivityEmail.this);
                    //    SharedHelper.putKey(mContext, "mobile", "+" + ccp.getSelectedCountryCode().toString() + email.getText().toString());
                        Intent mainIntent = new Intent(ActivityEmail.this, RegisterActivity.class);
                        mainIntent.putExtra("mobile", "+" + ccp.getSelectedCountryCode().toString()+email.getText().toString());
                        mainIntent.putExtra("signup", true);
                        mainIntent.putExtra("email", emailnew.getText().toString());
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }
                }else {
                    if (email.getText().toString().length()==0){
                        Toast.makeText(mContext,"Please Enter a Mobile Number",Toast.LENGTH_SHORT).show();
                    }else if (email.getText().toString().length()>10){
                        Toast.makeText(mContext,"Please Enter a valid Mobile Number",Toast.LENGTH_SHORT).show();
                    }else {
                        Utilities.hideKeyboard(ActivityEmail.this);
                        SharedHelper.putKey(ActivityEmail.this, "mobile", ccp.getSelectedCountryCodeWithPlus().toString() + email.getText().toString());
                        Intent mainIntent = new Intent(ActivityEmail.this, ActivityPassword.class);
                        mainIntent.putExtra("mobile", "+" + ccp.getSelectedCountryCode().toString()+email.getText().toString());
                        startActivity(mainIntent);
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    }
                }

             }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(ActivityEmail.this,"password", "");
                Utilities.hideKeyboard(ActivityEmail.this);
                Intent mainIntent = new Intent(ActivityEmail.this, RegisterActivity.class);
                mainIntent.putExtra("isFromMailActivity", true);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedHelper.putKey(ActivityEmail.this,"password", "");
                Utilities.hideKeyboard(ActivityEmail.this);
                Intent mainIntent = new Intent(ActivityEmail.this, ForgetPassword.class);
                mainIntent.putExtra("isFromMailActivity", true);
                startActivity(mainIntent);
            }
        });

    }

    public void displayMessage(String toastString){
        try{
            Snackbar.make(getCurrentFocus(),toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, toastString, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
    private boolean isValidEmaillId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }
}