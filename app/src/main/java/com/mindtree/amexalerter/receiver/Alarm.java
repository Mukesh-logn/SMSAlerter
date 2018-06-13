package com.mindtree.amexalerter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mindtree.amexalerter.service.SendEmailService;

/**
 * Created by M1030452 on 4/19/2018.
 */

public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, SendEmailService.class);
        context.startService(i);

    }
}
