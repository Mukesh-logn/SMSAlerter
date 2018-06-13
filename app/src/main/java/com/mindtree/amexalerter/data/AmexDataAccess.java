package com.mindtree.amexalerter.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mindtree.amexalerter.data.AmexDbDetailContract.*;

import java.io.Serializable;

/**
 * Created by M1030452 on 4/18/2018.
 */

public class AmexDataAccess implements Serializable {

    private static SQLiteDatabase db;
    private static SQLiteOpenHelper cardDbHelper;

    public AmexDataAccess(Context context) {
        cardDbHelper = new AmexDBHelper(context);
    }

    public void openDbToWrite() {
        db = cardDbHelper.getWritableDatabase();
    }

    public void close() {
        if (cardDbHelper != null) {
            cardDbHelper.close();
        }
    }

    public void openDbToRead() {
        db = cardDbHelper.getReadableDatabase();
    }

    public void insertAdminDetail(ContentValues contentValues) {
        db.insert(AdminEntity.TABLE_NAME_ADMIN, null, contentValues);
    }

    public Cursor getAdminDetail() {
        if (db != null) {
            return db.query(AdminEntity.TABLE_NAME_ADMIN, null,
                    null, null, null, null, null);
        } else
            return null;
    }

    public String getMPin() {
        if (db != null) {
            Cursor c = db.query(AdminEntity.TABLE_NAME_ADMIN, new String[]{AdminEntity.COLUMN_PIN},
                    null, null, null, null, null);
            if (c != null && c.moveToNext()) {
                return c.getString(c.getColumnIndex(AmexDbDetailContract.AdminEntity.COLUMN_PIN));
            }
        } else
            return "";
        return "";
    }

    public String getAdminNumber() {
        if (db != null) {
            Cursor c = db.query(AdminEntity.TABLE_NAME_ADMIN, new String[]{AdminEntity.COLUMN_ADMIN_NUMBER},
                    null, null, null, null, null);

            if (c != null && c.moveToNext()) {
                return c.getString(c.getColumnIndex(AmexDbDetailContract.AdminEntity.COLUMN_ADMIN_NUMBER));
            }
        }

        return "";
    }

    public String getUserNumber() {
        if (db != null) {
            Cursor c = db.query(AdminEntity.TABLE_NAME_ADMIN, new String[]{AdminEntity.COLUMN_USER_NUMBER},
                    null, null, null, null, null);

            if (c != null && c.moveToNext()) {
                return c.getString(c.getColumnIndex(AdminEntity.COLUMN_USER_NUMBER));
            }
        }

        return "";
    }

    public int updateAdminDetails(ContentValues values, String selection, String[] selectionArgs) {
        return db.update(AdminEntity.TABLE_NAME_ADMIN, values, selection, selectionArgs);
    }

    public void insertPhoneNumber(ContentValues contentValues) {
        db.insert(UserDetailEntity.TABLE_NAME_USER, null, contentValues);
    }

    public Cursor getPLevelNumber() {
        String[] c = {UserDetailEntity.COLUMN_USER_TYPE1};
        return db.query(UserDetailEntity.TABLE_NAME_USER, c,
                null, null, null, null, null);
    }

    public Cursor getS1LevelNumber() {
        String[] c = {UserDetailEntity.COLUMN_USER_TYPE2};
        return db.query(UserDetailEntity.TABLE_NAME_USER, c,
                null, null, null, null, null);
    }

    public Cursor getS2LevelNumber() {
        String[] c = {UserDetailEntity.COLUMN_USER_TYPE3};
        return db.query(UserDetailEntity.TABLE_NAME_USER, c,
                null, null, null, null, null);
    }

    public Cursor getAllMobileNumber() {
        return db.query(UserDetailEntity.TABLE_NAME_USER, null,
                null, null, null, null, null);
    }

    public void insetTicketDetails(ContentValues contentValues) {
        db.insert(TicketDetailEntity.TABLE_NAME_TICKET, null, contentValues);
    }

    public Cursor getAllTicket() {
        return db.query(TicketDetailEntity.TABLE_NAME_TICKET, null,
                null, null, null, null, null);
    }

    public int updateTicketDetails(ContentValues contentValues, String selection, String[] selectionArgs) {
        return db.update(TicketDetailEntity.TABLE_NAME_TICKET, contentValues, selection, selectionArgs);
    }

    public void deletePreviousMobileNumber() {
        if (db != null) {
            db.delete(UserDetailEntity.TABLE_NAME_USER, null, null);
        }
    }
}
