package com.reryde.app.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.hbb20.CountryCodePicker;
import com.reryde.app.Helper.CustomDialog;
import com.reryde.app.Helper.SharedHelper;
import com.reryde.app.Helper.URLHelper;
import com.reryde.app.R;
import com.reryde.app.RerydeApplication;
import com.reryde.app.Utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.reryde.app.RerydeApplication.trimMessage;

public class RRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    TextView logintext, agreeLink;

    EditText first_name_edt, last_name_edt, mobile_edt, email_edit,referal_edit;

    Button signup_btn;

    CountryCodePicker ccp;

    Context mContext = RRegisterActivity.this;

    CheckBox agreeCheck;

    Boolean agreeCheckVal = false;
    CustomDialog customDialog;
    Utilities utils = new Utilities();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rregister);

        FindviewbyId();
    }

    public void FindviewbyId() {


        logintext = findViewById(R.id.login_txt);
        agreeLink = findViewById(R.id.agreeLink);
        agreeCheck = findViewById(R.id.agreeCheck);
        first_name_edt = findViewById(R.id.first_name_edt);
//        first_name_edt.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
//        first_name_edt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
//        first_name_edt.setKeyListener(DigitsKeyListener.getInstance("qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM"));
        last_name_edt = findViewById(R.id.last_name_edt);
//        last_name_edt.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
//        last_name_edt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
//        last_name_edt.setKeyListener(DigitsKeyListener.getInstance("qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM"));
        mobile_edt = findViewById(R.id.mobile_edt);
        email_edit = findViewById(R.id.email_edit);
//        email_edit.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
//        email_edit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
//        email_edit.setKeyListener(DigitsKeyListener.getInstance("0123456789qwertzuiopasdfghjklyxcvbnm@."));
        referal_edit = findViewById(R.id.referal_edit);
        signup_btn = findViewById(R.id.signup_btn);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        signup_btn.setOnClickListener(this);
        logintext.setOnClickListener(this);
        agreeLink.setOnClickListener(this);

        agreeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    agreeCheckVal = true;
                } else {
                    agreeCheckVal = false;

                }
            }
        });
        String first = "Already Have an Account? ";
        String next = "<font color='#BD081C'>Login</font>";
        logintext.setText(Html.fromHtml(first + next));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_txt:
//                startActivity(new Intent(mContext, LoginActivity.class));
                Intent login_Intent = new Intent(mContext,LoginActivity.class);
                login_Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(login_Intent);
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                break;
            case R.id.signup_btn:
                RegisterValidation();
                break;
            case R.id.agreeLink:
                startActivity(new Intent(mContext, TermsActivity.class));
                break;
        }

    }

    public void RegisterValidation() {

        if (first_name_edt.getText().toString().length() == 0) {
            Toast.makeText(mContext, "Please Enter a Mobile Number", Toast.LENGTH_SHORT).show();
        } else if (last_name_edt.getText().toString().length() == 0) {
            Toast.makeText(mContext, "Please Enter a Mobile Number", Toast.LENGTH_SHORT).show();
        } else if (mobile_edt.getText().toString().length() == 0) {
            Toast.makeText(mContext, "Please Enter a Mobile Number", Toast.LENGTH_SHORT).show();
        } else if (mobile_edt.getText().toString().length() > 10) {
            Toast.makeText(mContext, "Please Enter a valid Mobile Number", Toast.LENGTH_SHORT).show();
        } else if (email_edit.getText().toString().length() == 0) {
            Toast.makeText(mContext, "Please Enter your Email", Toast.LENGTH_SHORT).show();
        } else if (!isValidEmaillId(email_edit.getText().toString())) {
            Toast.makeText(mContext, "Please Enter a Valid Email", Toast.LENGTH_SHORT).show();
        } else if (!agreeCheckVal) {
            Toast.makeText(mContext, "Kindly agree to terms and condition!", Toast.LENGTH_SHORT).show();

        } else {

            Utilities.hideKeyboard(RRegisterActivity.this);
            //    SharedHelper.putKey(mContext, "mobile", "+" + ccp.getSelectedCountryCode().toString() + email.getText().toString());
           /* Intent mainIntent = new Intent(RRegisterActivity.this, OTPVerification.class);
            mainIntent.putExtra("mobile", "+" + ccp.getSelectedCountryCode().toString() + mobile_edt.getText().toString());
            mainIntent.putExtra("status", "Register");
            mainIntent.putExtra("email", email_edit.getText().toString());
            mainIntent.putExtra("referal_code", referal_edit.getText().toString());
            mainIntent.putExtra("fname", first_name_edt.getText().toString());
            mainIntent.putExtra("lname", last_name_edt.getText().toString());
            startActivity(mainIntent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);*/

           if (!referal_edit.getText().toString().equalsIgnoreCase("")){
               checkreferral();
           }else {
               RegisterOTP();
           }

        }
    }

    private void checkreferral() {
        customDialog = new CustomDialog(RRegisterActivity.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("referral_code", referal_edit.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CHECK_REFFERAL,
                object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();

                RegisterOTP();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                String json = null;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    utils.print("MyTest", "" + error);
                    utils.print("MyTestError", "" + error.networkResponse);
                    utils.print("MyTestError1", "" + response.statusCode);
                    try {
                        if (response.statusCode == 422) {

                           /* json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }*/

                            displayMessage(getString(R.string.invalid_referral));

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
                        checkreferral();
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

    private void RegisterOTP() {

        customDialog = new CustomDialog(mContext);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        final String strMobileNumber =  "+" + ccp.getSelectedCountryCode().toString() + mobile_edt.getText().toString();
        String strEmailID = email_edit.getText().toString();
        try {
            if (strMobileNumber != null) {
                object.put("mobile", strMobileNumber);
                object.put("email", strEmailID);

            }
            Log.e("Login", "" + object + URLHelper.RegisterLogin);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.RegisterLogin, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                Log.e("response", "response" + response);

                SharedHelper.putKey(RRegisterActivity.this, "mobile", strMobileNumber);

                try {
                    if (response.getString("status").equals("true")) {
                        Toast.makeText(mContext, response.getString("message"), Toast.LENGTH_LONG).show();
//                        Toast.makeText(context,getString(R.string.registration_success),Toast.LENGTH_LONG).show();
                        GoToOTPActivity();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        displayMessage(errorObj.optString("message"));
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
                            if (errorObj.has("mobile") && errorObj.optJSONArray("mobile") != null)
                                displayMessage(errorObj.optJSONArray("mobile").opt(0).toString());
                            else if (errorObj.has("email") && errorObj.optJSONArray("email") != null)
                                displayMessage(errorObj.optJSONArray("email").opt(0).toString());

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

    private void GoToOTPActivity() {
          Intent mainIntent = new Intent(RRegisterActivity.this, OTPVerification.class);
            mainIntent.putExtra("mobile", "+" + ccp.getSelectedCountryCode().toString() + mobile_edt.getText().toString());
            mainIntent.putExtra("status", "Register");
            mainIntent.putExtra("email", email_edit.getText().toString());
            mainIntent.putExtra("referal_code", referal_edit.getText().toString());
            mainIntent.putExtra("fname", first_name_edt.getText().toString());
            mainIntent.putExtra("lname", last_name_edt.getText().toString());
            startActivity(mainIntent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    private void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(mContext, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    private boolean isValidEmaillId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
