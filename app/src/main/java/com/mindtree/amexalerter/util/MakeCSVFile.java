package com.mindtree.amexalerter.util;

import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import com.mindtree.amexalerter.data.AmexDataAccess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by M1030452 on 4/22/2018.
 */

public class MakeCSVFile {
    private String filename;

    public String createCSVFile(final AmexDataAccess amexDataAccess) throws IOException {
        amexDataAccess.openDbToRead();
        final Cursor cursor = amexDataAccess.getAllTicket();
        if (cursor != null) {
            File folder = new File(Environment.getExternalStorageDirectory()
                    + "/IVigilant");

            boolean var = false;
            if (!folder.exists())
                var = folder.mkdir();

            System.out.println("" + var);


            filename = folder.toString() + "/" + "TicketsDetail.csv";
            new Thread() {
                public void run() {
                    try {

                        FileWriter fw = new FileWriter(filename);
                        fw.append("SI NO");
                        fw.append(',');

                        fw.append("INC number");
                        fw.append(',');

                        fw.append("Severity");
                        fw.append(',');

                        fw.append("Queue Name");
                        fw.append(',');

                        fw.append("Description");
                        fw.append(',');

                        fw.append("Ticket Acceptance Time");
                        fw.append(',');

                        fw.append("Ticket Receive Time");
                        fw.append(',');

                        fw.append("Confirmation Message Time");
                        fw.append(',');

                        fw.append("Accepted by");
                        fw.append(',');

                        fw.append('\n');

                        if (cursor.moveToFirst()) {
                            do {
                                fw.append(cursor.getString(0));
                                fw.append(',');

                                fw.append(cursor.getString(1));
                                fw.append(',');

                                fw.append(cursor.getString(2));
                                fw.append(',');

                                fw.append(cursor.getString(3));
                                fw.append(',');

                                fw.append(cursor.getString(4));
                                fw.append(',');

                                fw.append(cursor.getString(5));
                                fw.append(',');

                                fw.append(cursor.getString(6));
                                fw.append(',');

                                fw.append(cursor.getString(7));
                                fw.append(',');

                                fw.append(cursor.getString(7));
                                fw.append(',');

                                fw.append('\n');

                            } while (cursor.moveToNext());
                        }
                        if (!cursor.isClosed()) {
                            cursor.close();
                        }

                        // fw.flush();
                        fw.close();

                        //Read CSV file
                       /* BufferedReader br = null;
                        String line = "";
                        String cvsSplitBy = ",";

                        br = new BufferedReader(new FileReader(filename));
                        while ((line = br.readLine()) != null) {

                            // use comma as separator
                            String[] data = line.split(cvsSplitBy);

                            System.out.println("Column1 [code= " + data[4] + " , Column2=" + data[5] + "]");

                        }*/

                    } catch (Exception e) {
                        Log.e("csv reeor", e.toString());
                    }
                }
            }.start();

        }
        return filename;
    }
}
