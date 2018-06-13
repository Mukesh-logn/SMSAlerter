package com.mindtree.amexalerter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by M1030452 on 4/22/2018.
 */

public class BasePermissionAppCompatActivity extends AppCompatActivity {

    private final static String APP_NAME = "APP_NAME";
    private final static int REQUEST_READ_SMS_PERMISSION = 113;
    private final static int WRITE_EXTERNAL_STORAGE = 114;

    RequestPermissionAction onPermissionCallBack;

    private boolean checkReadSMSPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void getReadSMSPermission(RequestPermissionAction onPermissionCallBack) {
        this.onPermissionCallBack = onPermissionCallBack;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkReadSMSPermission()) {
                requestPermissions(new String[]{Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
                return;
            }
        }
        if (onPermissionCallBack != null)
            onPermissionCallBack.smsReadPermissionGranted();
    }

    public boolean checkExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void getWriteExternalStoragePermission(RequestPermissionAction onPermissionCallBack) {
        this.onPermissionCallBack = onPermissionCallBack;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkExternalStoragePermission()) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
                return;
            }
        }
        if (onPermissionCallBack != null)
            onPermissionCallBack.externalStoragePermissionGranted();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (REQUEST_READ_SMS_PERMISSION == requestCode) {
                if (onPermissionCallBack != null) {
                    onPermissionCallBack.smsReadPermissionGranted();
                }
            }
            if (WRITE_EXTERNAL_STORAGE == requestCode) {
                if (onPermissionCallBack != null) {
                    onPermissionCallBack.externalStoragePermissionGranted();
                }
            }

        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            if (REQUEST_READ_SMS_PERMISSION == requestCode) {
                if (onPermissionCallBack != null) {
                    onPermissionCallBack.smsReadPermissionDenied();
                }
            }
            if (WRITE_EXTERNAL_STORAGE == requestCode) {
                if (onPermissionCallBack != null) {
                    onPermissionCallBack.externalStoragePermissionDenied();
                }
            }

        }
    }

    public interface RequestPermissionAction {
        void smsReadPermissionDenied();

        void smsReadPermissionGranted();

        void externalStoragePermissionDenied();

        void externalStoragePermissionGranted();
    }

}