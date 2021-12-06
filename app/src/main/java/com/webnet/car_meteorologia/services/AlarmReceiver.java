package com.webnet.car_meteorologia.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.webnet.car_meteorologia.activitys.LoginActivity;

public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Trigger the notification
        String msg = "";
        try{
            Bundle extr =intent.getExtras();
            msg = extr.getString("msg");
        }catch (Exception ex){}
        NotificationScheduler.showNotification(context, LoginActivity.class,
                "Recordatorio CAR", msg);
    }

}
