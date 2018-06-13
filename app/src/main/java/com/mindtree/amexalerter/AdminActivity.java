package com.mindtree.amexalerter;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mindtree.amexalerter.data.AmexDataAccess;
import com.mindtree.amexalerter.data.AmexDbDetailContract;
import com.mindtree.amexalerter.util.AppConstant;

import java.util.Date;

public class AdminActivity extends AppCompatActivity {
    TextInputEditText adminNumberInputText, adminNewNumber, mPinInputText;
    TextInputLayout textInputLayout8, textInputLayout9, textInputLayout10;
    private String mPin = "", adminNumber = "";
    Button button;
    AmexDataAccess amexDataAccess = new AmexDataAccess(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Intent intent = getIntent();
        if (intent != null) {
            adminNumber = intent.getStringExtra(AppConstant.ADMIN_NUMBER);
            mPin = intent.getStringExtra(AppConstant.M_PIN);
        }
        adminNumberInputText = findViewById(R.id.admin_number1);
        adminNewNumber = findViewById(R.id.new_admin_number);
        mPinInputText = findViewById(R.id.pin2);

        textInputLayout8 = findViewById(R.id.text_input_layout8);
        textInputLayout9 = findViewById(R.id.text_input_layout9);
        textInputLayout10 = findViewById(R.id.text_input_layout10);

        addTextWatcher(adminNumberInputText, textInputLayout8);
        addTextWatcher(adminNewNumber, textInputLayout9);
        addTextWatcher(mPinInputText, textInputLayout10);

        button = findViewById(R.id.submit_admin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textInputLayout8.getError()!=null&&textInputLayout9.getError()!=null&&textInputLayout10.getError()!=null&&
                        textInputLayout8.getError().equals("") && textInputLayout9.getError().equals("")
                        && textInputLayout10.getError().equals("")) {

                    amexDataAccess.openDbToWrite();
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

                        stringBuilder = stringBuilder.append(adminNumber);
                        notifyOtherMemberBySms(stringBuilder);
                    }


                    Toast.makeText(AdminActivity.this, "shortly you will get message of admin change request", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

    }

    private void notifyOtherMemberBySms(StringBuilder stringBuilder) {
        try {

            SmsManager sms = SmsManager.getDefault();

            String messageContent = "Admin number changed.old number : " + adminNumber +
                    " new number : " + String.valueOf(adminNewNumber.getText());
            String numberList = stringBuilder.toString();
            String numbers[] = numberList.split(";");

            for (String number : numbers) {
                if (number.length() >= 10) {
                    sms.sendTextMessage(number, null, messageContent, null, null);
                }
            }
            //updateAdminDetails();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Ops Error occurred, please try again later or try manually!",
                    Toast.LENGTH_LONG).show();

            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.putExtra("address", (CharSequence) stringBuilder);
            sendIntent.putExtra("sms_body", "Admin number changed.old number : " + adminNumber +
                    " new number : " + String.valueOf(adminNewNumber.getText()));
            sendIntent.setType("vnd.android-dir/mms-sms");
            startActivity(sendIntent);
            //updateAdminDetails();
            e.printStackTrace();
        }
    }

    private void updateAdminDetails() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(AmexDbDetailContract.AdminEntity.COLUMN_ADMIN_NUMBER, String.valueOf(adminNumberInputText.getText()));
        Date date = new Date();
        contentValues.put(AmexDbDetailContract.AdminEntity.COLUMN_ADMIN_UPDATE_TIME, String.valueOf(date));
        contentValues.put(AmexDbDetailContract.AdminEntity.COLUMN_ADMIN_NUMBER_UPDATED_BY, adminNumber);
        String selection = AmexDbDetailContract.AdminEntity.COLUMN_ADMIN_NUMBER + "=?";
        String[] selectionArgs = new String[]{adminNumber};
        amexDataAccess.updateAdminDetails(contentValues, selection, selectionArgs);
    }

    private void addTextWatcher(final TextInputEditText inputEditText, final TextInputLayout textInputLayout) {
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textInputLayout.setError("");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (inputEditText == adminNumberInputText) {
                    if (String.valueOf(adminNumberInputText.getText()).length() < 10) {
                        textInputLayout8.setError("10 digit phone number required");
                    } else if (!String.valueOf(adminNumberInputText.getText()).equals(adminNumber)) {
                        textInputLayout8.setError("Wrong admin number");
                    }
                }
                if (inputEditText == adminNewNumber) {
                    if (String.valueOf(adminNewNumber.getText()).length() < 10) {
                        textInputLayout9.setError("10 digit phone number required");
                    }
                }
                if (inputEditText == mPinInputText) {
                    if (String.valueOf(mPinInputText.getText()).equals("") || String.valueOf(mPinInputText.getText()) == null ||
                            String.valueOf(mPinInputText.getText()).length() < 4) {
                        textInputLayout10.setError("Please enter 4 digit pin");
                    } else if (!String.valueOf(mPinInputText.getText()).equals(mPin)) {
                        textInputLayout10.setError("Wrong pin");
                    }
                }


            }
        });
        inputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

    }
}
