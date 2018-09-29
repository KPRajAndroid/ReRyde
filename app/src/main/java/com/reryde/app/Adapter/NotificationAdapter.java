package com.reryde.app.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reryde.app.Helper.SharedHelper;
import com.reryde.app.Models.NotificatonModel;
import com.reryde.app.R;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Tranxit Technologies Pvt Ltd. on 30-03-2018.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    List<NotificatonModel> model;
    NotificationAdapter context = NotificationAdapter.this;

    public NotificationAdapter(List<NotificatonModel> models) {
        this.model = models;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NotificatonModel notificatonModel = model.get(position);

        if (notificatonModel.getDiscountType().equalsIgnoreCase("percent")){
            holder.discount.setText(notificatonModel.getDiscount() + "%" + " Offer on your next ride");
        }else {
            holder.discount.setText(notificatonModel.getCurrency() + notificatonModel.getDiscount() + " Offer on your next ride");
        }


//        holder.date_time.setText("expiration : " + notificatonModel.getExpiration());

        String input = notificatonModel.getExpiration();
        DateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = inputFormatter.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat outputFormatter = new SimpleDateFormat("MM/dd/yyyy");
        String output = outputFormatter.format(date); // Output : 01/20/2012

        holder.date_time.setText(output);

        holder.promo_code.setText("used code : " + notificatonModel.getPromoCode());

    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView discount, date_time, promo_code;

        public MyViewHolder(View itemView) {
            super(itemView);
            discount = itemView.findViewById(R.id.discount);
            date_time = itemView.findViewById(R.id.date_time);
            promo_code = itemView.findViewById(R.id.promo_code);

        }
    }
}
