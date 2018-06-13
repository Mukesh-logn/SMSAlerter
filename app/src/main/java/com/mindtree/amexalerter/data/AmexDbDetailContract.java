package com.mindtree.amexalerter.data;

import android.provider.BaseColumns;

/**
 * Created by M1030452 on 3/28/2018.
 */

public class AmexDbDetailContract {
    public static final class UserDetailEntity implements BaseColumns {
        public static final String TABLE_NAME_USER = "user";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_USER_TYPE1 = "P";
        public static final String COLUMN_USER_TYPE2 = "s1";
        public static final String COLUMN_USER_TYPE3 = "s3";
    }

    public static final class TicketDetailEntity implements BaseColumns {
        public static final String TABLE_NAME_TICKET = "ticket";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TICKET_INC = "inc";
        public static final String COLUMN_TICKET_SEVERITY = "severity";
        public static final String COLUMN_TICKET_QUEUE = "queue_name";
        public static final String COLUMN_TICKET_DESC = "ticket_desc";
        public static final String COLUMN_TICKET_ACCEPTANCE_TIME = "t1";
        public static final String COLUMN_TICKET_RECEIVE_TIME = "t2";
        public static final String COLUMN_ACCEPTANCE_MESSAGE_RECEIVING_TIME = "t3";
        public static final String COLUMN_TICKET_ACCEPTED_BY = "accepted_by";


    }

    public static final class AdminEntity implements BaseColumns {
        public static final String TABLE_NAME_ADMIN = "admin";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ADMIN_NUMBER = "admin_num";
        public static final String COLUMN_USER_NUMBER = "user_num";
        public static final String COLUMN_ADMIN_EAMIL = "target_email";
        public static final String COLUMN_CC_EMAIL = "cc_email";
        public static final String COLUMN_ADMIN_UPDATE_TIME = "update_time";
        public static final String COLUMN_ADMIN_NUMBER_UPDATED_BY = "updated_by";
        public static final String COLUMN_PIN = "m_pin";


    }
}
