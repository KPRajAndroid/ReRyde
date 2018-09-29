package com.reryde.app.Activities;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.reryde.app.Helper.ConnectionHelper;
import com.reryde.app.Helper.CustomDialog;
import com.reryde.app.Helper.SharedHelper;
import com.reryde.app.Helper.URLHelper;
import com.reryde.app.R;
import com.reryde.app.RerydeApplication;
import com.reryde.app.Utils.MyButton;
import com.reryde.app.Utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.reryde.app.RerydeApplication.trimMessage;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    CountryCodePicker ccp;
    EditText mobie_number_txt;
    ImageView backArrow;
    Button confirm_btn,Facebook_btn;
    TextView signup_txt;
    String device_token, device_UDID;

    String TAG = "LoginActivity";

    CallbackManager callbackManager;
    Boolean isInternet;
    ConnectionHelper helper;

    CustomDialog customDialog;
    Utilities utils = new Utilities();
    Context context = LoginActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FindviewById();
    }

    public void FindviewById() {
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        mobie_number_txt = findViewById(R.id.mobie_number_txt);
        backArrow = findViewById(R.id.backArrow);
        confirm_btn = findViewById(R.id.confirm_btn);
        Facebook_btn = findViewById(R.id.Facebook_btn);
        signup_txt = findViewById(R.id.signup_txt);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        GetToken();

        helper = new ConnectionHelper(LoginActivity.this);
        isInternet = helper.isConnectingToInternet();

        callbackManager = CallbackManager.Factory.create();
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {

        }

        backArrow.setOnClickListener(this);
        confirm_btn.setOnClickListener(this);
        Facebook_btn.setOnClickListener(this);
        signup_txt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backArrow:
                finish();
                break;
            case R.id.confirm_btn:
                LoginValidation();
                break;
            case R.id.Facebook_btn:
                FBLogin();
                break;
            case R.id.signup_txt:
                startActivity(new Intent(context, RRegisterActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
        }
    }


    private void loadTermsView(final String loginBy)
    {
        try
        {
            final AlertDialog.Builder termsViewBuilder = new AlertDialog.Builder(LoginActivity.this);
            View loadView = getLayoutInflater().inflate(R.layout.terms_view, null);

            termsViewBuilder.setView(loadView);

            final AlertDialog termsDialog = termsViewBuilder.create();
            termsDialog.show();

//            termsView = (WebView) loadView.findViewById(R.id.help_view);
//            progressBar = (ProgressBar) loadView.findViewById(R.id.progressBar);
//
//            termsView.setWebViewClient(new ActivitySocialLogin.myWebClient());
//            termsView.getSettings().setJavaScriptEnabled(true);
//            termsView.loadUrl("http://reryde.com/terms");

            MyButton cancelBTN = (MyButton) loadView.findViewById(R.id.cancelBTN);
            MyButton proceedBTN = (MyButton) loadView.findViewById(R.id.proceedBTN);

            cancelBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    termsDialog.dismiss();
                    finish();
                }
            });

            proceedBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    termsDialog.dismiss();
                    if ( loginBy.equalsIgnoreCase("facebook") )
                    {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkReadSMSPermission()) {
                                requestPermissions(new String[]{Manifest.permission.READ_SMS}, 100);
                            }else{
                                FBLogin();
                            }
                        }else {
                            FBLogin();
                        }


                    }
                }
            });
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void FBLog(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkReadSMSPermission()) {
                requestPermissions(new String[]{Manifest.permission.READ_SMS}, 100);
            }else{
                FBLogin();
            }
        }else {
            FBLogin();
        }
    }


    private void FBLogin() {
            if (isInternet) {

                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile","email"));


                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {


                            public void onSuccess(LoginResult loginResult) {
                                if (AccessToken.getCurrentAccessToken() != null) {
                                    Log.i("loginresult", "" + loginResult.getAccessToken().getToken());
                                    SharedHelper.putKey(LoginActivity.this, "accessToken", loginResult.getAccessToken().getToken());
                                    SharedHelper.putKey(context,"login_by","facebook");
//                                    phoneLogin();
                                        login(loginResult.getAccessToken().getToken(), URLHelper.FACEBOOK_LOGIN, "facebook");
                                }

                            }

                            @Override
                            public void onCancel() {
                                // App code
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                // App code
                            }
                        });


            } else {
                //mProgressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("Check your Internet").setCancelable(false);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent NetworkAction = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(NetworkAction);

                    }
                });
                builder.show();
            }

    }

    public void GetToken() {
        try {
            if (!SharedHelper.getKey(context, "device_token").equals("") && SharedHelper.getKey(context, "device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                utils.print(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = ""+ FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(context, "device_token",""+FirebaseInstanceId.getInstance().getToken());
                utils.print(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            utils.print(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            utils.print(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            utils.print(TAG, "Failed to complete device UDID");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("public_profile", "email"));

                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {

                            public void onSuccess(LoginResult loginResult) {
                                if (AccessToken.getCurrentAccessToken() != null) {
                                    Log.i("loginresult", "" + loginResult.getAccessToken().getToken());
                                    SharedHelper.putKey(LoginActivity.this, "accessToken", loginResult.getAccessToken().getToken());
//                                    accessToken = loginResult.getAccessToken().getToken();
//                                    login(accessToken, URLHelper.FACEBOOK_LOGIN, "facebook");
                                } else {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            }

                            @Override
                            public void onCancel() {
                                // App code
                                displayMessage(getResources().getString(R.string.fb_cancel));
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                // App code
                                displayMessage(getResources().getString(R.string.fb_error));
                            }
                        });
            }
        }
    }

    public void login(final String accesstoken, final String URL, final String Loginby) {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        final JsonObject json = new JsonObject();
        json.addProperty("device_type", "android");
        json.addProperty("device_token", device_token);
        json.addProperty("accessToken", accesstoken);
        json.addProperty("device_id", device_UDID);
        json.addProperty("login_by", Loginby);
        Log.e("LoginActivity", "login: Facebook" + json);
        Ion.with(LoginActivity.this)
                .load(URL)
                .addHeader("X-Requested-With", "XMLHttpRequest")
//                .addHeader("Authorization",""+SharedHelper.getKey(context, "token_type")+" "+SharedHelper.getKey(context, "access_token"))
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if ((customDialog != null) && customDialog.isShowing())
                            customDialog.dismiss();
                        if (e != null) {
                            if (e instanceof NetworkErrorException) {
                                displayMessage(getString(R.string.oops_connect_your_internet));
                            } else if (e instanceof TimeoutException) {
                                login(accesstoken, URL, Loginby);
                            } else{
                                displayMessage(getString(R.string.please_try_again));
                            }
                            return;
                        }
                        if (result != null) {
                            Log.v(Loginby + "_Response", result.toString());
                            try {
                                JSONObject jsonObject = new JSONObject(result.toString());
                                String status = jsonObject.optString("status");
                                if (status.equalsIgnoreCase("true")) {
                                    SharedHelper.putKey(LoginActivity.this, "token_type", jsonObject.optString("token_type"));
                                    SharedHelper.putKey(LoginActivity.this, "access_token", jsonObject.optString("access_token"));
                                    if (Loginby.equalsIgnoreCase("facebook"))
                                        SharedHelper.putKey(LoginActivity.this, "login_by", "facebook");

                                    if (!jsonObject.optString("currency").equalsIgnoreCase("") && jsonObject.optString("currency") != null)
                                        SharedHelper.putKey(context, "currency", jsonObject.optString("currency"));
                                    else
                                        SharedHelper.putKey(context, "currency", "$");
                                    getProfile();
                                }else if (jsonObject.optString("status").equalsIgnoreCase("false")){
                                    String strMessage = jsonObject.optString("message");
//                                    getProfile();
                                    displayMessage(strMessage);
                                }

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                                if (Loginby.equalsIgnoreCase("facebook")) {
                                    displayMessage(getResources().getString(R.string.fb_error));
                                } else {
                                    displayMessage(getResources().getString(R.string.google_login));
                                }
                            }
                        }else {
                            displayMessage(getString(R.string.please_try_again));
                        }
                        // onBackPressed();
                    }
                });
    }

    public void LoginValidation() {
        if (mobie_number_txt.getText().toString().length() == 0) {
//            Toast.makeText(context, "Please Enter a Mobile Number", Toast.LENGTH_SHORT).show();
            displayMessage("Please Enter a Mobile Number");
        } else if (!(mobie_number_txt.getText().toString().length() < 20)) {
//            Toast.makeText(context, "Please Enter a valid Mobile Number", Toast.LENGTH_SHORT).show();
            displayMessage("Please Enter a valid Mobile Number");
        } else {
            Utilities.hideKeyboard(LoginActivity.this);

            /*SharedHelper.putKey(LoginActivity.this, "mobile", ccp.getSelectedCountryCodeWithPlus().toString() + mobie_number_txt.getText().toString());
            Intent mainIntent = new Intent(LoginActivity.this, OTPVerification.class);
            mainIntent.putExtra("mobile", "+" + ccp.getSelectedCountryCode().toString() + mobie_number_txt.getText().toString());
            mainIntent.putExtra("status", "Login");
            startActivity(mainIntent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);*/

            LoginAPI();

        }


    }

    public void GoToLoginActivity() {
        Intent mainIntent = new Intent(context, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
    }

    public void getProfile() {

        if (isInternet) {
            customDialog = new CustomDialog(context);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.UserProfile+"?device_type=android&device_id="+device_UDID+"&device_token="+device_token, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    Log.v("GetProfile", response.toString());

                    SharedHelper.putKey(context, "id", response.optString("id"));
                    SharedHelper.putKey(context, "first_name", response.optString("first_name"));
                    SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                    SharedHelper.putKey(context, "email", response.optString("email"));
                    if (response.optString("picture").startsWith("http"))
                        SharedHelper.putKey(context, "picture", response.optString("picture"));
                    else
                        SharedHelper.putKey(context, "picture", URLHelper.base + "storage/" + response.optString("picture"));
                    SharedHelper.putKey(context, "gender", response.optString("gender"));
                    SharedHelper.putKey(context, "mobile", response.optString("mobile"));
                    SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));

                    SharedHelper.putKey(context, "referral_earning", response.optString("referral_earning"));
                    SharedHelper.putKey(context, "referral_code", response.optString("referral_code"));

                    //SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));
                    if (response.optString("payment_mode").equalsIgnoreCase("PAYPAL") || response.optString("payment_mode").equalsIgnoreCase("CASH"))
                        SharedHelper.putKey(context, "payment_mode", "PAYPAL");
                    else
                        SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));

                    if (!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                        SharedHelper.putKey(context, "currency", response.optString("currency"));
                    else
                        SharedHelper.putKey(context, "currency", "$");
                    SharedHelper.putKey(context, "sos", response.optString("sos"));
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.True));

                    if (!response.optString("mobile").equalsIgnoreCase("")){
                        SharedHelper.putKey(context, "mobile", response.optString("mobile"));
                        GoToMainActivity();
                    }else {
                        GoTOPhoneNumber();
                    }



                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
//                                refreshAccessToken();
                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

                            } else if (response.statusCode == 503) {
                                displayMessage(getString(R.string.server_down));
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } catch (Exception e) {
                            displayMessage(getString(R.string.something_went_wrong));
                        }

                    } else {
                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            getProfile();
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };

            RerydeApplication.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }

    private void GoTOPhoneNumber() {
        Intent mainIntent = new Intent(context, PhoneNumberActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    private void LoginAPI() {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        String strMobileNumber = ccp.getSelectedCountryCodeWithPlus().toString() + mobie_number_txt.getText().toString();
        try {
            if (strMobileNumber != null) {
                object.put("mobile", strMobileNumber);
            }
            Log.e("Login", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.Login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                displayMessage(response.optString("message"));
                Log.e("response", "response" + response);
                GoToLogin();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    Log.e("MyTest", "" + error);
                    Log.e("MyTestError", "" + error.networkResponse);
                    Log.e("MyTestError1", "" + response.statusCode);
                    Log.e("MyTestError1", "" + response.data);
                    Log.e("MyTestError1", "" + new String(response.data));
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            try {
                                if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                    //   Refresh token
                                } else {
                                    displayMessage(errorObj.optString("message"));
                                }
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 422) {
                            displayMessage(errorObj.optString("message"));
                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                if (json.startsWith("The selected mobile is invalid.")) {

//                                    displayMessage(getString(R.string.invalid_email));
                                    Toast.makeText(context,getString(R.string.invalid_email),Toast.LENGTH_LONG).show();

                                    GoToRegister();

                                } else {
                                    displayMessage(json);
                                    //displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }


                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        // registerAPI();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        RerydeApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    private void GoToLogin() {
        SharedHelper.putKey(LoginActivity.this, "mobile", ccp.getSelectedCountryCodeWithPlus().toString() + mobie_number_txt.getText().toString());
        Intent mainIntent = new Intent(LoginActivity.this, OTPVerification.class);
        mainIntent.putExtra("mobile", "+" + ccp.getSelectedCountryCode().toString() + mobie_number_txt.getText().toString());
        mainIntent.putExtra("status", "Login");
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void GoToRegister() {
        SharedHelper.putKey(LoginActivity.this, "mobile", ccp.getSelectedCountryCodeWithPlus().toString() + mobie_number_txt.getText().toString());
            Intent mainIntent = new Intent(LoginActivity.this, RRegisterActivity.class);
           /* mainIntent.putExtra("mobile", "+" + ccp.getSelectedCountryCode().toString() + mobie_number_txt.getText().toString());
            mainIntent.putExtra("status", "Register");*/
            startActivity(mainIntent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    private void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkReadSMSPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (data != null) {

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
