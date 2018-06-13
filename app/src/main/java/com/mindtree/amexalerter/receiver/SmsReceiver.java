package com.mindtree.amexalerter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.mindtree.amexalerter.data.AmexDataAccess;
import com.mindtree.amexalerter.service.SendTicketNotificationService;
import com.mindtree.amexalerter.service.SaveRosterDataService;

/**
 * Created by M1030452 on 4/17/2018.
 */
public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getExtras();
        AmexDataAccess amexDataAccess = new AmexDataAccess(context);
        amexDataAccess.openDbToRead();
        amexDataAccess.getAdminDetail();
        String adminNumber = amexDataAccess.getAdminNumber();
        try {
            if (bundle != null) {
                String phoneNumber = "", message = "";

                final Object[] pDusObject = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pDusObject.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pDusObject[i]);
                    phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: " + phoneNumber + "; message: " + message + "; ServiceCenterAddress: " + currentMessage.getServiceCenterAddress());
                }
                if (message.startsWith("0M SEV") || message.startsWith("15M SEV") || message.startsWith("30M SEV")
                        || message.startsWith("45M SEV")
                        || message.startsWith("Following ticket has been accepted by")) {
                    Intent i = new Intent(context, SendTicketNotificationService.class);
                    i.putExtras(bundle);
                    context.startService(i);

                } else if (phoneNumber.contains(adminNumber)) {
                    Intent i = new Intent(context, SaveRosterDataService.class);
                    i.putExtras(bundle);
                    i.putExtra(SaveRosterDataService.AMEX_ACCESS_OBJECT, amexDataAccess);
                    context.startService(i);
                }
            }


        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }
    }
}
