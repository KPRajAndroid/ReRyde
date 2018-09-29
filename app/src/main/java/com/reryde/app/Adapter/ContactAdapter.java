package com.reryde.app.Adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.reryde.app.Fragments.PastTrips;
import com.reryde.app.Models.ContactModel;
import com.reryde.app.R;

import java.util.ArrayList;
import java.util.List;




/**
 * Created by Tranxit Technologies Pvt Ltd. on 17-03-2018.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {
    Activity activity;
   ArrayList<ContactModel> model;
    CallBackInterface callBackInterface;

    public ContactAdapter(Activity activity , ArrayList<ContactModel> model, CallBackInterface callBackInterface){
        this.activity = activity;
        this.model=model;
        this.callBackInterface = callBackInterface;

    }
    public  interface  CallBackInterface{
         void AddRemove(String status ,String values,int position);

    }
    @Override
    public ContactAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactAdapter.MyViewHolder holder, final int position) {
        ContactModel contact = model.get(position)  ;
        if(contact.getProfilePicture()!=null && !contact.getProfilePicture().isEmpty()){
            holder.profile_pic.setImageURI(Uri.parse(contact.getProfilePicture()));
        }else {
            holder.profile_pic.setImageResource(R.drawable.ic_dummy_user);

        }if(contact.getName()!=null && !contact.getName().isEmpty()){
            holder.name_txt.setText(contact.getName());
        }if(contact.getPhoneNumber()!=null && !contact.getPhoneNumber().isEmpty()){
            holder.Phone_number_txt.setText(contact.getPhoneNumber());
        }

        holder.select_contact.setChecked(model.get(position).isSelected());

        holder.select_contact.setTag(position);
        holder.select_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                if (model.get(pos).isSelected()){
                    model.get(pos).setSelected(false);
                    callBackInterface.AddRemove("remove",CheckMobilNumber(model.get(pos).getPhoneNumber()),pos);
                }
                else {
                    model.get(pos).setSelected(true);

                        callBackInterface.AddRemove("add",CheckMobilNumber(model.get(pos).getPhoneNumber()),pos);


                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return model.size();
    }
    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_pic;
        TextView name_txt,Phone_number_txt;
        CheckBox select_contact;
        public MyViewHolder(View itemView) {
            super(itemView);
            profile_pic = itemView.findViewById(R.id.profile_pic);
            name_txt = itemView.findViewById(R.id.name_txt);
            Phone_number_txt = itemView.findViewById(R.id.Phone_number_txt);
            select_contact = itemView.findViewById(R.id.select_contact);
        }
    }

 public static String CheckMobilNumber(final String MobileNumber){
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            // phone must begin with '+'
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(MobileNumber, "");
            int countryCode = numberProto.getCountryCode();
            return MobileNumber;
        } catch (NumberParseException e) {
            return "+91"+MobileNumber;
        }
    }



}
