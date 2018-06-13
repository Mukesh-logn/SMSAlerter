package com.mindtree.amexalerter.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;
import android.util.Log;

import com.mindtree.amexalerter.NotificationManagerActivity;
import com.mindtree.amexalerter.R;
import com.mindtree.amexalerter.data.AmexDataAccess;
import com.mindtree.amexalerter.data.AmexDbDetailContract;
import com.mindtree.amexalerter.data.AmexDbDetailContract.TicketDetailEntity;

import java.util.Date;

/**
 * Created by M1030452 on 4/17/2018.
 */

public class SendTicketNotificationService extends Service {
    AmexDataAccess amexDataAccess = new AmexDataAccess(this);
    String ticketMessageBody[];
    String[] acceptanceMesageBody;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public SendTicketNotificationService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // bundle is null
        super.onStartCommand(intent, flags, startId);
        Bundle bundle = null;
        if (intent != null) {
            bundle = intent.getExtras();
        }

        if (bundle != null) {
            amexDataAccess.openDbToWrite();
            String phoneNumber = "", message = "";

            final Object[] pDusObject = (Object[]) bundle.get("pdus");
            for (int i = 0; i < pDusObject.length; i++) {
                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pDusObject[i]);
                phoneNumber = currentMessage.getDisplayOriginatingAddress();
                message = currentMessage.getDisplayMessageBody();

                Log.i("SmsReceiver", "senderNum: " + phoneNumber + "; message: " + message + "; ServiceCenterAddress: " + currentMessage.getServiceCenterAddress());
            }
            if (message.startsWith("0M SEV") || message.startsWith("15M SEV") || message.startsWith("30M SEV")
                    || message.startsWith("45M SEV")) {
                NotifyUserAndSaveTicket(bundle, message);
            } else if (message.startsWith("Following ticket has been accepted by")) {
                acceptanceMesageBody = message.split(" ");
                Intent intent1 = new Intent(this, PlaySoundService.class);
                stopService(intent1);
                NotificationManager nMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (nMgr != null) {
                    nMgr.cancel(Integer.parseInt(acceptanceMesageBody[12].substring(3)));
                }
                updateTicketDetails();
            }
        }
        return Service.START_NOT_STICKY;
    }

    private void updateTicketDetails() {
        ContentValues contentValues = new ContentValues();
        Date date = new Date();
        contentValues.put(TicketDetailEntity.COLUMN_ACCEPTANCE_MESSAGE_RECEIVING_TIME, String.valueOf(date));
        contentValues.put(TicketDetailEntity.COLUMN_TICKET_ACCEPTED_BY, acceptanceMesageBody[7]);

        String selection = TicketDetailEntity.COLUMN_TICKET_INC + "=?";
        String[] selectionArgs = new String[]{acceptanceMesageBody[12]};

        amexDataAccess.updateTicketDetails(contentValues, selection, selectionArgs);


    }

    private void NotifyUserAndSaveTicket(Bundle bundle, String message) {
        ticketMessageBody = message.split(" ", 5);
        sendNotification(bundle, message);
        ContentValues contentValues = new ContentValues();
        contentValues.put(TicketDetailEntity.COLUMN_TICKET_SEVERITY, ticketMessageBody[1]);
        contentValues.put(TicketDetailEntity.COLUMN_TICKET_QUEUE, ticketMessageBody[2]);
        contentValues.put(TicketDetailEntity.COLUMN_TICKET_INC, ticketMessageBody[3]);
        contentValues.put(TicketDetailEntity.COLUMN_TICKET_DESC, ticketMessageBody[4]);
        Date date = new Date();
        contentValues.put(TicketDetailEntity.COLUMN_TICKET_RECEIVE_TIME, String.valueOf(date));
        amexDataAccess.insetTicketDetails(contentValues);
    }

    private void sendNotification(Bundle bundle, String messageDetails) {
        Cursor c = amexDataAccess.getAllMobileNumber();
        String userPhoneNumber = amexDataAccess.getUserNumber();
        if (c != null) {
            String separator = "; ";
                    /*if (android.os.Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
                        separator = ", ";
                    }*/
            StringBuilder stringBuilder = new StringBuilder();
            while (c.moveToNext()) {

                String p = c.getString(c.getColumnIndex(AmexDbDetailContract.UserDetailEntity.COLUMN_USER_TYPE1));
                String s1 = c.getString(c.getColumnIndex(AmexDbDetailContract.UserDetailEntity.COLUMN_USER_TYPE2));
                String s2 = c.getString(c.getColumnIndex(AmexDbDetailContract.UserDetailEntity.COLUMN_USER_TYPE3));
                if (p != null && p.length() >= 10) {
                    if (p.contains(userPhoneNumber)) {
                        ring(bundle, messageDetails);
                    }
                }
                if (s1 != null && s1.length() >= 10) {
                    if (s1.contains(userPhoneNumber)) {
                        ring(bundle, messageDetails);
                    }
                }
                if (s2 != null && s2.length() >= 10) {
                    if (s2.contains(userPhoneNumber)) {
                        ring(bundle, messageDetails);
                    }
                }
            }

        }


    }

    private void ring(Bundle bundle, String messageDetails) {
        Intent i = new Intent(this, NotificationManagerActivity.class);
        i.putExtras(bundle);
        i.setAction(Long.toString(System.currentTimeMillis()));
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        /*Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();*/
        /*Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
        MediaPlayer player = MediaPlayer.create(this, notification);
        player.setLooping(true);
        player.start();*/
        Intent intent = new Intent(this, PlaySoundService.class);
        startService(intent);

        // build notification
        Notification n = new Notification.Builder(this)
                .setContentTitle("Amex Alert")
                .setContentText(messageDetails)
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pIntent)
                .setAutoCancel(false)
                .setOngoing(true)
                .addAction(R.drawable.ic_launcher_foreground, "More", pIntent)
                .build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(Integer.parseInt(ticketMessageBody[3].substring(3)), n);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("Service", "service destroid");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}


