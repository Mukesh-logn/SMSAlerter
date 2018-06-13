package com.mindtree.amexalerter;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.mindtree.amexalerter.data.AmexDataAccess;
import com.mindtree.amexalerter.data.AmexDbDetailContract;
import com.mindtree.amexalerter.service.PlaySoundService;
import com.mindtree.amexalerter.service.SendTicketNotificationService;

import java.util.Date;


public class NotificationManagerActivity extends AppCompatActivity {
    String phoneNumber = "", message = "";
    String[] MessageBody;
    AmexDataAccess amexDataAccess = new AmexDataAccess(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        if (b != null) {

            final Object[] pDusObject = (Object[]) b.get("pdus");
            for (int i = 0; i < pDusObject.length; i++) {
                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pDusObject[i]);
                phoneNumber = currentMessage.getDisplayOriginatingAddress();
                message = currentMessage.getDisplayMessageBody();

                Log.i("SmsReceiverInActivity", "senderNum: " + phoneNumber + "; message: " + message + "; ServiceCenterAddress: " + currentMessage.getServiceCenterAddress());
            }
            // String[] messageContentToBeSend = message.split(" ", 3);
        }

        setContentView(R.layout.activity_notification_manager);
        FragmentManager fm = getSupportFragmentManager();
        final AmexDialogFragment editNameDialogFragment = new AmexDialogFragment();
        editNameDialogFragment.setCancelable(false);
        editNameDialogFragment.setArguments(b);
        editNameDialogFragment.setListener(new AmexDialogFragment.Listener() {
            @Override
            public void onAcceptButtonClick() {

                AmexDataAccess amexDataAccess = new AmexDataAccess(NotificationManagerActivity.this);
                amexDataAccess.openDbToWrite();
                updateTicketAcceptanceTime(amexDataAccess);
                Cursor c = amexDataAccess.getAllMobileNumber();
                if (c != null) {
                    String separator = "; ";
                    /*if (android.os.Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
                        separator = ", ";
                    }*/
                    StringBuilder stringBuilder = new StringBuilder();
                    while (c.moveToNext()) {

                        String s = c.getString(c.getColumnIndex(AmexDbDetailContract.UserDetailEntity.COLUMN_USER_TYPE1));
                        String s1 = c.getString(c.getColumnIndex(AmexDbDetailContract.UserDetailEntity.COLUMN_USER_TYPE2));
                        String s2 = c.getString(c.getColumnIndex(AmexDbDetailContract.UserDetailEntity.COLUMN_USER_TYPE3));
                        if (s != null && s.length() >= 10) {
                            stringBuilder = stringBuilder.append(s).append(separator);
                        }
                        if (s1 != null && s1.length() >= 10) {
                            stringBuilder = stringBuilder.append(s1).append(separator);
                        }
                        if (s2 != null && s2.length() >= 10) {
                            stringBuilder = stringBuilder.append(s2).append(separator);
                        }
                    }
                    editNameDialogFragment.dismiss();
                    notifyOtherMemberBySms(stringBuilder);
                }

            }
        });
        editNameDialogFragment.show(fm, "ticket");
    }

    private void updateTicketAcceptanceTime(AmexDataAccess amexDataAccess) {
        ContentValues contentValues = new ContentValues();
        Date date = new Date();
        contentValues.put(AmexDbDetailContract.TicketDetailEntity.COLUMN_TICKET_ACCEPTANCE_TIME, String.valueOf(date));

        String selection = AmexDbDetailContract.TicketDetailEntity.COLUMN_TICKET_INC + "=?";
        MessageBody = message.split(" ", 5);
        String[] selectionArgs = new String[]{MessageBody[3]};
        amexDataAccess.updateTicketDetails(contentValues, selection, selectionArgs);
    }

    private void notifyOtherMemberBySms(StringBuilder stringBuilder) {
        try {
            /*Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.putExtra("address", (CharSequence) stringBuilder);
            sendIntent.putExtra("sms_body", "Following ticket has been accepted by: " + phoneNumber + "\n" +
                    "Ticket Details :" + message);
            sendIntent.setType("vnd.android-dir/mms-sms");
            startActivity(sendIntent);*/

            SmsManager sms = SmsManager.getDefault();

            String messageContent = "Following ticket has been accepted by: " + amexDataAccess.getUserNumber() +
                    " Ticket Details: " + message;
            String numberList = stringBuilder.toString();
            String numbers[] = numberList.split(";");

            for (String number : numbers) {
                if (number.length() >= 10) {
                    sms.sendTextMessage(number, null, messageContent, null, null);
                }
            }
            Intent intent = new Intent(this, PlaySoundService.class);
            stopService(intent);
            NotificationManager nMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (nMgr != null) {
                nMgr.cancel(Integer.parseInt(MessageBody[3].substring(3)));
            }
            Intent intent1 = new Intent(this, SendTicketNotificationService.class);
            stopService(intent);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
