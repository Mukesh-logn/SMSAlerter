package com.mindtree.amexalerter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.mindtree.amexalerter.util.TextCaptchaWithBackground;

/**
 * Created by M1030452 on 4/17/2018.
 */

public class AmexDialogFragment extends DialogFragment {
    private Listener listener;
    Bundle bundle;
    String message = "";
    TableLayout tl;
    ImageView captchaImageView;
    TextInputEditText edtTextCaptcha;
    TextInputLayout textInputLayout;
    TextCaptchaWithBackground textCaptchaWithBackground;
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public AmexDialogFragment() {
        super();

    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.bundle = args;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_ticket_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Object[] pDusObject = (Object[]) bundle.get("pdus");
        tl = view.findViewById(R.id.table);
        captchaImageView = view.findViewById(R.id.captcha_image);
        textInputLayout =view.findViewById(R.id.captcha_text_input);
        edtTextCaptcha =view.findViewById(R.id.captcha_text);

        for (int i = 0; i < pDusObject.length; i++) {
            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pDusObject[i]);
            message = currentMessage.getDisplayMessageBody();
        }
        textCaptchaWithBackground = new TextCaptchaWithBackground(350, 100,
                4, TextCaptchaWithBackground.TextOptions.LETTERS_ONLY);
        setAlertBox();
        view.findViewById(R.id.accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textCaptchaWithBackground.checkAnswer(edtTextCaptcha.getText().toString().trim())) {
                    listener.onAcceptButtonClick();
                } else {
                    Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                    edtTextCaptcha.startAnimation(shake);
                    textInputLayout.setError("Wrong Captcha");
                }
            }
        });

    }

    private void setAlertBox() {
        String s[] = message.split(" ", 5);
        TableRow tr1 = new TableRow(getContext());
        tr1.setLayoutParams(getLayoutParams());
        tr1.addView(getTextView(1, "INC number", Color.BLACK, Typeface.NORMAL, Color.WHITE));
        tr1.addView(getTextView(1, s[3], Color.BLACK, Typeface.NORMAL, Color.WHITE));
        tl.addView(tr1, getTblLayoutParams());
        TableRow tr2 = new TableRow(getContext());
        tr2.setLayoutParams(getLayoutParams());
        tr2.addView(getTextView(2, "Severity", Color.BLACK, Typeface.NORMAL, Color.WHITE));
        tr2.addView(getTextView(2, s[1], Color.BLACK, Typeface.NORMAL, Color.WHITE));
        tl.addView(tr2, getTblLayoutParams());
        TableRow tr3 = new TableRow(getContext());
        tr3.setLayoutParams(getLayoutParams());
        tr3.addView(getTextView(3, "Queue Name", Color.BLACK, Typeface.NORMAL, Color.WHITE));
        tr3.addView(getTextView(3, s[2], Color.BLACK, Typeface.NORMAL, Color.WHITE));
        tl.addView(tr3, getTblLayoutParams());
        TableRow tr4 = new TableRow(getContext());
        tr4.setLayoutParams(getLayoutParams());
        tr4.addView(getTextView(4, "Description", Color.BLACK, Typeface.NORMAL, Color.WHITE));
        tr4.addView(getTextView(4, s[4], Color.BLACK, Typeface.NORMAL, Color.WHITE));
        tl.addView(tr4, getTblLayoutParams());
        captchaImageView.setImageBitmap(textCaptchaWithBackground.getImage());


    }

    private TextView getTextView(int id, String title, int color, int typeface, int bgColor) {
        TextView tv = new TextView(getContext());
        tv.setId(id);
        tv.setText(title.toUpperCase());
        tv.setTextColor(color);
        tv.setPadding(20, 20, 20, 20);
        tv.setTypeface(Typeface.DEFAULT, typeface);
        tv.setBackgroundColor(bgColor);
        tv.setLayoutParams(getLayoutParams());
        return tv;
    }

    @NonNull
    private LayoutParams getLayoutParams() {
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        params.setMargins(2, 0, 0, 2);
        return params;
    }

    @NonNull
    private TableLayout.LayoutParams getTblLayoutParams() {
        return new TableLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
    }

    interface Listener {
        void onAcceptButtonClick();
    }
}
