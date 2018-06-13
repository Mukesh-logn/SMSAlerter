package com.mindtree.amexalerter.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;
import android.util.Log;

import com.mindtree.amexalerter.data.AmexDataAccess;
import com.mindtree.amexalerter.data.AmexDbDetailContract;

import java.util.Date;

/**
 * Created by M1030452 on 4/18/2018.
 */

public class SaveRosterDataService extends Service {
    public static final String AMEX_ACCESS_OBJECT = "amex_access";
    AmexDataAccess amexDataAccess;

    public SaveRosterDataService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Bundle bundle = null;
        if (intent != null) {
            bundle = intent.getExtras();

            amexDataAccess = (AmexDataAccess) intent.getSerializableExtra(AMEX_ACCESS_OBJECT);
            if (bundle != null) {
                String phoneNumber = "", message = "";

                final Object[] pDusObject = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pDusObject.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pDusObject[i]);
                    phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    message = currentMessage.getDisplayMessageBody();
                    if (message.startsWith("Admin number changed")) {
                        saveAdminDetails(message, phoneNumber);
                    } else {
                        saveRosterMessage(message);
                    }
                    Log.i("SmsReceiver", "senderNum: " + phoneNumber + "; message: " + message + "; ServiceCenterAddress: " + currentMessage.getServiceCenterAddress());
                }

            }
        }

        return Service.START_STICKY;
    }

    private void saveRosterMessage(String message) {
        amexDataAccess.deletePreviousMobileNumber();
        String[] mobileNumbers = message.split(" ");
        for (int i = 0; i < mobileNumbers.length; i++) {
            ContentValues contentValues = new ContentValues();
            String parts[] = mobileNumbers[i].split(":");
            if (parts[0].equals("P")) {
                contentValues.put(AmexDbDetailContract.UserDetailEntity.COLUMN_USER_TYPE1, parts[1]);
            } else if (parts[0].equals("S1")) {
                contentValues.put(AmexDbDetailContract.UserDetailEntity.COLUMN_USER_TYPE2, parts[1]);
            } else if (parts[0].equals("S2")) {
                contentValues.put(AmexDbDetailContract.UserDetailEntity.COLUMN_USER_TYPE3, parts[1]);
            }
            amexDataAccess.openDbToWrite();
            amexDataAccess.insertPhoneNumber(contentValues);
        }
    }

    private void saveAdminDetails(String message, String phoneNumber) {
        String[] s = message.split(" ");
        if (phoneNumber.length() > 10) {
            phoneNumber = phoneNumber.substring(phoneNumber.length() - 10);
            ;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(AmexDbDetailContract.AdminEntity.COLUMN_ADMIN_NUMBER, s[9]);
        Date date = new Date();
        contentValues.put(AmexDbDetailContract.AdminEntity.COLUMN_ADMIN_UPDATE_TIME, String.valueOf(date));
        contentValues.put(AmexDbDetailContract.AdminEntity.COLUMN_ADMIN_NUMBER_UPDATED_BY, phoneNumber);
        String selection = AmexDbDetailContract.AdminEntity.COLUMN_ADMIN_NUMBER + "=?";
        String[] selectionArgs = new String[]{phoneNumber};
        int index = amexDataAccess.updateAdminDetails(contentValues, selection, selectionArgs);
        Log.d("update Admin details", String.valueOf(index));
    }
}
