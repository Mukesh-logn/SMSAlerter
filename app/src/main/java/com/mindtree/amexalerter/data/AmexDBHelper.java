package com.mindtree.amexalerter.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mindtree.amexalerter.data.AmexDbDetailContract.*;

import java.util.Date;

/**
 * Created by M1030452 on 3/28/2018.
 */

public class AmexDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "amex.db";
    private static final int VERSION_NUMBER = 1;
    private Context context;

    public AmexDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION_NUMBER);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE_ADMIN = "CREATE TABLE " + AdminEntity.TABLE_NAME_ADMIN + "" + "("
                + AdminEntity._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + AdminEntity.COLUMN_ADMIN_NUMBER + " TEXT , "
                + AdminEntity.COLUMN_USER_NUMBER + " TEXT , "
                + AdminEntity.COLUMN_ADMIN_EAMIL + " TEXT, "
                + AdminEntity.COLUMN_CC_EMAIL + " TEXT, "
                + AdminEntity.COLUMN_ADMIN_UPDATE_TIME + " TEXT, "
                + AdminEntity.COLUMN_ADMIN_NUMBER_UPDATED_BY + " TEXT, "
                + AdminEntity.COLUMN_PIN + " TEXT "
                +
                ");";

        String CREATE_TABLE_USER = "CREATE TABLE " + UserDetailEntity.TABLE_NAME_USER + "" + "("
                + UserDetailEntity._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UserDetailEntity.COLUMN_USER_TYPE1 + " TEXT , "
                + UserDetailEntity.COLUMN_USER_TYPE2 + " TEXT, "
                + UserDetailEntity.COLUMN_USER_TYPE3 + " TEXT "
                +
                ");";

        String CREATE_TABLE_TICKET = "CREATE TABLE " + TicketDetailEntity.TABLE_NAME_TICKET + "" + "("
                + TicketDetailEntity._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TicketDetailEntity.COLUMN_TICKET_INC + " TEXT , "
                + TicketDetailEntity.COLUMN_TICKET_SEVERITY + " TEXT, "
                + TicketDetailEntity.COLUMN_TICKET_QUEUE + " TEXT, "
                + TicketDetailEntity.COLUMN_TICKET_DESC + " TEXT, "
                + TicketDetailEntity.COLUMN_TICKET_ACCEPTANCE_TIME + " TEXT, "
                + TicketDetailEntity.COLUMN_TICKET_RECEIVE_TIME + " TEXT, "
                + TicketDetailEntity.COLUMN_ACCEPTANCE_MESSAGE_RECEIVING_TIME + " TEXT, "
                + TicketDetailEntity.COLUMN_TICKET_ACCEPTED_BY + " TEXT "
                +
                ");";

        sqLiteDatabase.execSQL(CREATE_TABLE_ADMIN);
        sqLiteDatabase.execSQL(CREATE_TABLE_USER);
        sqLiteDatabase.execSQL(CREATE_TABLE_TICKET);

        ContentValues contentValues = new ContentValues();
        contentValues.put(AdminEntity.COLUMN_ADMIN_NUMBER, "8660525004");
        contentValues.put(AdminEntity.COLUMN_ADMIN_EAMIL, "ramydv77@gmail.com");
        contentValues.put(AdminEntity.COLUMN_PIN, "1111");
        Date d = new Date();
        contentValues.put(AdminEntity.COLUMN_ADMIN_UPDATE_TIME, String.valueOf(d));
        sqLiteDatabase.insert(AmexDbDetailContract.AdminEntity.TABLE_NAME_ADMIN, null, contentValues);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserDetailEntity.TABLE_NAME_USER);
        onCreate(sqLiteDatabase);
    }
}
