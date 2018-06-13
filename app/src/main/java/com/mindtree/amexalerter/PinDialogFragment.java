package com.mindtree.amexalerter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by M1030452 on 4/22/2018.
 */

public class PinDialogFragment extends DialogFragment {
    private Listener listener;
    TextInputLayout textInputLayout;
    TextInputEditText textInputPin;
    String mPin;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public PinDialogFragment() {
        super();

    }

    public void setArguments(String pin) {
        this.mPin = pin;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_pin_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textInputPin = view.findViewById(R.id.pin);
        textInputLayout = view.findViewById(R.id.text_input_layout1);
        textInputPin.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        textInputPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String mPinText = textInputPin.getText().toString();
                if (mPinText.length() == 4) {
                    if (mPinText.equals(mPin)) {
                      listener.showLogs();
                    } else {
                        textInputLayout.setError("Wrong Pin");
                    }
                } else {
                    textInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }


    interface Listener {
        void showLogs();
    }
}
