package com.reryde.app.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.appsflyer.AppsFlyerLib;
import com.google.firebase.iid.FirebaseInstanceId;
import com.reryde.app.Fragments.ContactsList;
import com.reryde.app.Helper.SharedHelper;
import com.reryde.app.Helper.URLHelper;
import com.reryde.app.R;
import com.reryde.app.RerydeApplication;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.reryde.app.RerydeApplication.trimMessage;

public class FreeRides extends AppCompatActivity implements View.OnClickListener {

    String TAG = "FreeRides";
    ImageView select_contact, backArrow,select_share;
    Button invite_friends;
    Context context = FreeRides.this;
    TextView referal_code_id, referal_amount_txt,content;
    String device_token, device_UDID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_rides);
        select_contact = findViewById(R.id.select_contact);
        select_share = findViewById(R.id.select_share);
        backArrow = findViewById(R.id.backArrow);
        invite_friends = findViewById(R.id.invite_friends);
        referal_amount_txt = findViewById(R.id.referal_amount_txt);
        content = findViewById(R.id.content);
        referal_code_id = findViewById(R.id.referal_code_id);
        GetToken();
        getprofile();

        content.setText("Give the Referral bonus by reffering the invite code worth "+ SharedHelper.getKey(context, "currency")+"10.");

        referal_code_id.setText(SharedHelper.getKey(context, "referral_code"));
        if (SharedHelper.getKey(context, "referral_earning") != null && !SharedHelper.getKey(context, "referral_earning").equalsIgnoreCase("null")) {
            referal_amount_txt.setText(SharedHelper.getKey(context, "currency") + SharedHelper.getKey(context, "referral_earning"));

        } else {
            referal_amount_txt.setText(SharedHelper.getKey(context, "currency") + "0");

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkStoragePermission()){
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }else{
                getcountrycode();
            }
        } else{
            getcountrycode();
        }
        select_contact.setOnClickListener(this);
        select_share.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        invite_friends.setOnClickListener(this);
    }

    public void GetToken() {
        try {
            if(!SharedHelper.getKey(context,"device_token").equals("") && SharedHelper.getKey(context,"device_token") != null) {
                device_token = SharedHelper.getKey(context, "device_token");
                Log.i(TAG, "GCM Registration Token: " + device_token);
            }else{
                device_token = ""+ FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(context, "device_token",""+FirebaseInstanceId.getInstance().getToken());
                Log.i(TAG, "Failed to complete token refresh: " + device_token);
            }
        }catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh", e);
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            Log.i(TAG, "Device UDID:" + device_UDID);
        }catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
    }

    private void getprofile() {
        Log.e("GetPostAPI",""+ URLHelper.UserProfile+"?device_type=android&device_id="+device_UDID+"&device_token="+device_token);
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.UserProfile+"" +
                "?device_type=android&device_id="+device_UDID+"&device_token="+device_token, object , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println("enetr the profile data api"+response);

                SharedHelper.putKey(context, "id", response.optString("id"));
                SharedHelper.putKey(context, "first_name", response.optString("first_name"));
                SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                SharedHelper.putKey(context, "email", response.optString("email"));
                if (response.optString("picture").startsWith("http"))
                    SharedHelper.putKey(context, "picture", response.optString("picture"));
                else
                    SharedHelper.putKey(context, "picture", URLHelper.base+"storage/"+response.optString("picture"));
                SharedHelper.putKey(context, "gender", response.optString("gender"));
                SharedHelper.putKey(context, "mobile", response.optString("mobile"));
                SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
                SharedHelper.putKey(context, "referral_earning", response.optString("referral_earning"));
                SharedHelper.putKey(context, "referral_code", response.optString("referral_code"));

                //SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));
                if(response.optString("payment_mode").equalsIgnoreCase("PAYPAL") || response.optString("payment_mode").equalsIgnoreCase("CASH"))
                    SharedHelper.putKey(context, "payment_mode", "PAYPAL");
                else
                    SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));

                if(!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                    SharedHelper.putKey(context, "currency",response.optString("currency"));
                else
                    SharedHelper.putKey(context, "currency","$");
                SharedHelper.putKey(context,"sos",response.optString("sos"));
                Log.e(TAG, "onResponse: Sos Call" + response.optString("sos"));
                SharedHelper.putKey(context,"loggedIn", context.getResources().getString(R.string.True));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if(response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500){
                            try{
                                displayMessage(errorObj.optString("message"));
                            }catch (Exception e){
                                displayMessage(context.getResources().getString(R.string.something_went_wrong));
                            }
                        }else if(response.statusCode == 401){
//                            refreshAccessToken();
                        }else if(response.statusCode == 422){

                            json = trimMessage(new String(response.data));
                            if(json !="" && json != null) {
                                displayMessage(json);
                            }else{
                                displayMessage(context.getResources().getString(R.string.please_try_again));
                            }

                        }else if(response.statusCode == 503){
                            displayMessage(context.getResources().getString(R.string.server_down));
                        }
                    }catch (Exception e){
                        displayMessage(context.getResources().getString(R.string.something_went_wrong));
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(context.getResources().getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        getprofile();
                    }
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization",""+SharedHelper.getKey(context, "token_type")+" "+SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        RerydeApplication.getInstance().addToRequestQueue(jsonObjectRequest);

    }


    private void moveToFragment(Fragment fragment) {
        if (!fragment.isAdded()) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.addToBackStack(null);
            transaction.add(R.id.free_run_layout, fragment, "Test Fragment");
            transaction.commit();
        }


    }

    @Override
    public void onClick(View v) {
        Fragment fragment = new ContactsList();
        switch (v.getId()) {
            case R.id.select_contact:
                moveToFragment(fragment);
                break;
            case R.id.select_share:

                Map<String, Object> share_eventValue = new HashMap<String, Object>();
                AppsFlyerLib.getInstance().trackEvent(context,"af_click_share",share_eventValue);

                navigateToShareScreen(referal_code_id.getText().toString());
                break;
            case R.id.backArrow:
                finish();
                break;
            case R.id.invite_friends:
                moveToFragment(fragment);
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    getcountrycode();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED;
    }

    public void getcountrycode() {
        TelephonyManager tm = (TelephonyManager) getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String phNo = tm.getLine1Number();
        String country = tm.getSimCountryIso();

//        Log.d("PhoneNumber :", phNo);
//        Log.d("country :", country);


    }

    public void displayMessage(String toastString){
        Log.e("displayMessage",""+toastString);
        Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
    }

    public void navigateToShareScreen(String shareUrl) {
        try{
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String shareMessage = "Hey, I'm using ReRyde for the most affordable ride in town.  Download ReRyde and use my  referral code "+shareUrl+" to save up to $10 on your second ride! " + URLHelper.APP_URL+getPackageName()+"&hl=en";
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, "Share applications not found!", Toast.LENGTH_SHORT).show();
        }

    }

}
