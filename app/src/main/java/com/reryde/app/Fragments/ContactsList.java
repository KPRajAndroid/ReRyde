package com.reryde.app.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.reryde.app.Activities.RegisterActivity;
import com.reryde.app.Adapter.ContactAdapter;
import com.reryde.app.Helper.CustomDialog;
import com.reryde.app.Helper.SharedHelper;
import com.reryde.app.Models.ContactModel;
import com.reryde.app.R;
import com.reryde.app.Retrofit.ApiInterface;
import com.reryde.app.Retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.reryde.app.Adapter.ContactAdapter.CheckMobilNumber;

public class ContactsList extends Fragment implements ContactAdapter.CallBackInterface {
    ArrayList<ContactModel> model = new ArrayList<ContactModel>();
  //  List<String> mobile = new ArrayList<String>();
    HashMap<String, String> mobile = new HashMap<>();
    private Activity activity;

    CustomDialog customDialog;
    View view;
    RecyclerView recyclerView;
    ImageView backArrow;
    CheckBox allcheck;
    Button send_referal_btn;
    ContactAdapter contactAdapter;
    ApiInterface mApiInterface = null;
    Context context;
    EditText search_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        context = getContext();
        view = inflater.inflate(R.layout.fragment_contacts_list, container, false);
        FindviewbyID(view);
        mApiInterface = RetrofitClient.getLiveTrackingClient().create(ApiInterface.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkStoragePermission()){
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
            }else{
                getContactList();
            }

        } else{
            getContactList();
        }

        return view;


    }

    private void getContactList() {
        new AsyncTaskRunner().execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    getContactList();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED;
    }

    public void FindviewbyID(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
        backArrow = view.findViewById(R.id.backArrow);
        allcheck = view.findViewById(R.id.allcheck);
        send_referal_btn = view.findViewById(R.id.send_referal_btn);
        search_bar = view.findViewById(R.id.search_bar);
        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        allcheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    setIsSelectedAll(true);
                    contactAdapter.notifyDataSetChanged();
                    setadapter();
                }else {
                    setIsSelectedAll(false);
                    contactAdapter.notifyDataSetChanged();
                    setadapter();
                }



            }
        });
        send_referal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mobile!=null && !mobile.isEmpty()){
                    SendReferalcode();
                }else {
                    Toast.makeText(getContext(),"Please Select Mobile Number",Toast.LENGTH_LONG).show();
                }

            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }


    public void getContactList1() {

        if(model!=null){
            model.clear();
        }
        ContentResolver cr = activity.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String picture = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                        model.add(new ContactModel(name, phoneNo, picture));
                        Log.e("model", "model: " + model.size());

                        Log.i("somt", "Name: " + name);
                        Log.i("somt", "Phone Number: " + phoneNo);
                        Log.i("somt", "User Picture " + picture);
                        setadapter();
                    }

                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }


    }
    public void setadapter(){
        RemoveTheDuplicateData(model);
        contactAdapter = new ContactAdapter(activity,RemoveTheDuplicateData(model),this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contactAdapter);
    }

    public ArrayList<ContactModel> RemoveTheDuplicateData(ArrayList<ContactModel> contactsLists){
        for(int i=0;i<contactsLists.size();i++){
            for(int j=i+1;j<contactsLists.size();j++){
                if(contactsLists.get(i).equals(contactsLists.get(j))){
                    contactsLists.remove(j);
                    j--;
                }
            }
        }
        return contactsLists;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setIsSelectedAll(boolean checktrue){
        for (int i = 0;model.size()>i;i++){
            if(checktrue){
                model.get(i).setSelected(true);
               // mobile.put(CheckMobilNumber(model.get(i).getPhoneNumber()));
                mobile.put("mobile["+i+"]", CheckMobilNumber(model.get(i).getPhoneNumber()));

            }else {
                model.get(i).setSelected(false);
              //  mobile.remove(CheckMobilNumber(model.get(i).getPhoneNumber()));
                mobile.remove("mobile["+i+"]");

            }

        }
    }




    @Override
    public void AddRemove(String status, String values,int position) {
        if(status.equalsIgnoreCase("add")){
           // mobile.add(values);
            mobile.put("mobile["+position+"]",values);


        }else {
           // mobile.remove(values);
            mobile.remove("mobile["+position+"]");
        }

    }
    public void SendReferalcode(){
        Call<ResponseBody> call = mApiInterface. sendReferal("XMLHttpRequest", SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"),mobile, "user","android");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println("enetr the response code"+response.code());
                if(response.isSuccessful() && response.body()!=null){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        System.out.println("enter the arralist api"+jsonObject);
                        Toast.makeText(context,jsonObject.optString("message"),Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else {
                    Toast.makeText(context,"Something Went Wrong", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    private class AsyncTaskRunner extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                if (!customDialog.isShowing()) {
                    customDialog.show();
                }

                model.clear();

        }

        @Override
        protected String doInBackground(String... strings) {

            ContentResolver cr = activity.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            if ((cur != null ? cur.getCount() : 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));

                    if (cur.getInt(cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String picture = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                            model.add(new ContactModel(name, phoneNo, picture));
                            Log.e("model", "model: " + model.size());

                            Log.i("somt", "Name: " + name);
                            Log.i("somt", "Phone Number: " + phoneNo);
                            Log.i("somt", "User Picture " + picture);
//                            setadapter();
                        }

                        Collections.sort(model, new Comparator<ContactModel>() {
                            @Override
                            public int compare(ContactModel o1, ContactModel o2) {
                                return o1.getName().compareTo(o2.getName());
                            }

                        });

                        pCur.close();
                    }
                }
            }
            if (cur != null) {
                cur.close();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (customDialog.isShowing()) {
                customDialog.dismiss();
            }

            setadapter();

        }
    }
}
