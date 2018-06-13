package com.mindtree.amexalerter;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.mindtree.amexalerter.data.AmexDataAccess;
import com.mindtree.amexalerter.data.AmexDbDetailContract.AdminEntity;
import com.mindtree.amexalerter.util.AppConstant;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingActivity extends AppCompatActivity {
    TextInputEditText adminNumberInputText, targetEmail, ccEmail, mPinInputText, oldPin, newPin, confirmPin;
    TextInputLayout textInputLayout1, textInputLayout2, textInputLayout3, textInputLayout4,
            textInputLayout5, textInputLayout6, textInputLayout7;
    Button submit, changePinTextView;
    AmexDataAccess amexDataAccess = new AmexDataAccess(this);
    private String adminPhoneNumber, userPhoneNumber, targetEmailAddress, CCEmail, mPin;
    ViewFlipper viewFlipperTextView;
    TextView changeAdminNumber;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        adminNumberInputText = findViewById(R.id.admin_number);
        targetEmail = findViewById(R.id.email);
        ccEmail = findViewById(R.id.cc_email);
        mPinInputText = findViewById(R.id.pin);
        submit = findViewById(R.id.submit);
        oldPin = findViewById(R.id.old_pin);
        newPin = findViewById(R.id.new_pin);
        confirmPin = findViewById(R.id.confirmPin);

        changePinTextView = findViewById(R.id.Change_pin_button);
        viewFlipperTextView = findViewById(R.id.view_flipper);
        final Switch s = findViewById(R.id.switch_button);
        final TextView changeDetailText = findViewById(R.id.change_details_text);
        final TextView ChangePinText = findViewById(R.id.change_pin_text);

        textInputLayout1 = findViewById(R.id.text_input_layout1);
        textInputLayout2 = findViewById(R.id.text_input_layout2);
        textInputLayout3 = findViewById(R.id.text_input_layout3);
        textInputLayout4 = findViewById(R.id.text_input_layout4);
        textInputLayout5 = findViewById(R.id.text_input_layout5);
        textInputLayout6 = findViewById(R.id.text_input_layout6);
        textInputLayout7 = findViewById(R.id.text_input_layout7);

        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        viewFlipperTextView.setInAnimation(in);
        viewFlipperTextView.setOutAnimation(out);
        amexDataAccess.openDbToWrite();

        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (s.isChecked()) {
                    viewFlipperTextView.showNext();
                    changeDetailText.setTextColor(getResources().getColor(R.color.colorLight));
                    ChangePinText.setTextColor(getResources().getColor(R.color.colorAccent));
                } else {
                    viewFlipperTextView.showPrevious();
                    ChangePinText.setTextColor(getResources().getColor(R.color.colorLight));
                    changeDetailText.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
        });


        Cursor c = amexDataAccess.getAdminDetail();
        if (c != null && c.moveToNext()) {
            adminPhoneNumber = c.getString(c.getColumnIndex(AdminEntity.COLUMN_ADMIN_NUMBER));
            userPhoneNumber = c.getString(c.getColumnIndex(AdminEntity.COLUMN_USER_NUMBER));
            targetEmailAddress = c.getString(c.getColumnIndex(AdminEntity.COLUMN_ADMIN_EAMIL));
            CCEmail = c.getString(c.getColumnIndex(AdminEntity.COLUMN_CC_EMAIL));
            mPin = c.getString(c.getColumnIndex(AdminEntity.COLUMN_PIN));
        }



        /*adminNumberInputText.setText(adminPhoneNumber);
        targetEmail.setText(targetEmailAddress);
        ccEmail.setText(CCEmail);*/

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (String.valueOf(adminNumberInputText.getText()).length() < 1 && String.valueOf(targetEmail.getText()).length() < 1
                        && String.valueOf(ccEmail.getText()).length() < 1) {
                    Toast.makeText(SettingActivity.this, "Please Enter details", Toast.LENGTH_LONG).show();
                    return;
                }
                if (String.valueOf(adminNumberInputText.getText()).length() > 0 && String.valueOf(adminNumberInputText.getText()).length() < 10) {
                    textInputLayout1.setError("Please enter 10 digit phone number");
                    return;
                }
                if (String.valueOf(targetEmail.getText()).length() > 0 && !validate(String.valueOf(targetEmail.getText()))) {
                    textInputLayout2.setError("Enter valid Email");
                    return;

                }
                if (String.valueOf(mPinInputText.getText()).equals("") || String.valueOf(mPinInputText.getText()) == null ||
                        String.valueOf(mPinInputText.getText()).length() < 4) {
                    textInputLayout4.setError("Please enter 4 digit pin");
                    return;
                } else if (String.valueOf(mPinInputText.getText()).equals(mPin)) {
                    textInputLayout4.setError("Wrong pin code, Enter correct one.");
                    return;
                }

                if (textInputLayout4.getError().equals("")) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(AdminEntity.COLUMN_USER_NUMBER, String.valueOf(adminNumberInputText.getText()));
                    contentValues.put(AdminEntity.COLUMN_ADMIN_EAMIL, String.valueOf(targetEmail.getText()));
                    contentValues.put(AdminEntity.COLUMN_CC_EMAIL, String.valueOf(ccEmail.getText()));
                    contentValues.put(AdminEntity.COLUMN_ADMIN_NUMBER_UPDATED_BY, userPhoneNumber);
                    Date date = new Date();
                    contentValues.put(AdminEntity.COLUMN_ADMIN_UPDATE_TIME, String.valueOf(date));
                    contentValues.put(AdminEntity.COLUMN_ADMIN_NUMBER_UPDATED_BY, userPhoneNumber);
                    /*String selection = AdminEntity.COLUMN_ADMIN_NUMBER + "=?";
                    String[] selectionArgs = new String[]{userPhoneNumber};*/
                    amexDataAccess.updateAdminDetails(contentValues, null, null);
                    Toast.makeText(SettingActivity.this, "Details updated successfully", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

        changePinTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (String.valueOf(oldPin.getText()).equals("") || String.valueOf(oldPin.getText()) == null ||
                        String.valueOf(oldPin.getText()).length() < 4 || String.valueOf(newPin.getText()).equals("") ||
                        String.valueOf(newPin.getText()) == null || String.valueOf(newPin.getText()).length() < 4 ||
                        String.valueOf(confirmPin.getText()).equals("") || String.valueOf(confirmPin.getText()) == null
                        || String.valueOf(confirmPin.getText()).length() < 4) {
                    Toast.makeText(SettingActivity.this, "Please enter details correctly", Toast.LENGTH_LONG).show();
                    return;
                }
                if (textInputLayout5.getError().equals("") && textInputLayout6.getError().equals("")
                        && textInputLayout7.getError().equals("")) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(AdminEntity.COLUMN_PIN, String.valueOf(newPin.getText()));
                    String selection = AdminEntity.COLUMN_PIN + "=?";
                    String[] selectionArgs = new String[]{String.valueOf(oldPin.getText())};
                    amexDataAccess.updateAdminDetails(contentValues, selection, selectionArgs);
                    Toast.makeText(SettingActivity.this, "Pin changed successfully", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(SettingActivity.this, "Please enter all filed correctly",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        changeAdminNumber = findViewById(R.id.button_change_admin_number);
        changeAdminNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, AdminActivity.class);
                adminPhoneNumber = amexDataAccess.getAdminNumber();
                intent.putExtra(AppConstant.ADMIN_NUMBER, adminPhoneNumber);
                intent.putExtra(AppConstant.M_PIN, mPin);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        addTextWatcher(adminNumberInputText, textInputLayout1);
        addTextWatcher(targetEmail, textInputLayout2);
        addTextWatcher(ccEmail, textInputLayout3);
        addTextWatcher(mPinInputText, textInputLayout4);
        addTextWatcher(oldPin, textInputLayout5);
        addTextWatcher(newPin, textInputLayout6);
        addTextWatcher(confirmPin, textInputLayout7);
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
                if (inputEditText == oldPin) {
                    if (String.valueOf(oldPin.getText()).equals("") || String.valueOf(oldPin.getText()) == null ||
                            String.valueOf(oldPin.getText()).length() < 4) {
                        textInputLayout5.setError("Please enter 4 digit pin");
                    } else if (!String.valueOf(oldPin.getText()).equals(mPin)) {
                        textInputLayout5.setError("Wrong pin,Please Enter correct one.");
                    }
                }
                if (inputEditText == newPin) {
                    if (String.valueOf(newPin.getText()).equals("") || String.valueOf(newPin.getText()) == null ||
                            String.valueOf(newPin.getText()).length() < 4) {
                        textInputLayout6.setError("Please enter 4 digit new pin");
                    } else if (String.valueOf(oldPin.getText()).equals(String.valueOf(newPin.getText()))) {
                        textInputLayout6.setError("New pin should be different from old pin");
                    }
                }
                if (inputEditText == confirmPin) {
                    String newPinCode = String.valueOf(newPin.getText());
                    String confirmPinCode = String.valueOf(confirmPin.getText());
                    if (String.valueOf(confirmPin.getText()).equals("") || String.valueOf(confirmPin.getText()) == null || String.valueOf(confirmPin.getText()).length() < 4) {
                        textInputLayout7.setError("Please enter 4 confirmation pin");
                    } else if (!newPinCode.equals(confirmPinCode)) {
                        textInputLayout7.setError("Entered pin do not match");
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

    public boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}
