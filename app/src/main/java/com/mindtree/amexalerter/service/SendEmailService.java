package com.mindtree.amexalerter.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mindtree.amexalerter.data.AmexDataAccess;
import com.mindtree.amexalerter.util.MakeCSVFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by M1030452 on 4/19/2018.
 */

public class SendEmailService extends Service {
    private String adminPhoneNumber, adminEmailId, CCEmail;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public SendEmailService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        final AmexDataAccess amexDataAccess = new AmexDataAccess(getApplication());
        try {
            Cursor c = amexDataAccess.getAdminDetail();
            if (c != null && c.moveToFirst()) {
                adminEmailId = c.getString(3);
                CCEmail = c.getString(4);
            }
            if (adminEmailId != null) {
                MakeCSVFile makeCSVFile = new MakeCSVFile();
                final String filename = makeCSVFile.createCSVFile(amexDataAccess);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendMail(filename, amexDataAccess);
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void sendMail(String filename, AmexDataAccess amexDataAccess) {
        String userNumber = amexDataAccess.getUserNumber();
       //userNumber = "9148673475";
        String host = "smtp.gmail.com";
        final String user = "amsvc2018@gmail.com";//change accordingly
        final String password = "Just4N0w";//change accordingly

        //Get the session object
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");


        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });

        try {

            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(user));
            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(adminEmailId));
            if (CCEmail != null) {
                InternetAddress[] myCcList = InternetAddress.parse(CCEmail);
                message.addRecipients(Message.RecipientType.CC, myCcList);
            }

            // Set Subject: header field
            Date date = new Date();
            String subject = "iVigilant log " + date + " " + userNumber + "";
            message.setSubject(subject);

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setText("PFA");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            Transport.send(message);
            // Send message
            Log.v("Email", "Email send successful");
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
