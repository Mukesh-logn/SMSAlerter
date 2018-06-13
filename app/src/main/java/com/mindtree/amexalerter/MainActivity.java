package com.mindtree.amexalerter;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mindtree.amexalerter.data.AmexDataAccess;
import com.mindtree.amexalerter.data.AmexDbDetailContract;
import com.mindtree.amexalerter.data.AmexDbDetailContract.TicketDetailEntity;
import com.mindtree.amexalerter.receiver.Alarm;
import com.mindtree.amexalerter.service.SendTicketNotificationService;
import com.mindtree.amexalerter.util.MakeCSVFile;
import com.mindtree.amexalerter.util.TicketAdapter;
import com.mindtree.amexalerter.util.TicketDetail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.mail.Session;

public class MainActivity extends BasePermissionAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BasePermissionAppCompatActivity.RequestPermissionAction {
    private static final int CODE_WRITE_SETTINGS_PERMISSION = 112;
    public AmexDataAccess amexDataAccess = new AmexDataAccess(this);
    RecyclerView recyclerView;
    TicketAdapter ticketAdapter;
    Session session = null;
    private List<TicketDetail> ticketDetailList;
    Button saveFile;

    @Override
    public void getReadSMSPermission(RequestPermissionAction onPermissionCallBack) {
        super.getReadSMSPermission(onPermissionCallBack);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public boolean checkExternalStoragePermission() {
        return super.checkExternalStoragePermission();
    }

    @Override
    public void getWriteExternalStoragePermission(RequestPermissionAction onPermissionCallBack) {
        super.getWriteExternalStoragePermission(onPermissionCallBack);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new GetAllTicketListTask().execute();
        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        saveFile = findViewById(R.id.button_save_file);

        saveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if (checkExternalStoragePermission()) {
                        createFile();
                    } else {
                        getWriteExternalStoragePermission(MainActivity.this);
                    }


                } else {
                    createFile();
                }
            }
        });

        //get sms permission
        getReadSMSPermission(this);

        // get write permission
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                // If do not have write settings permission then open the Can modify system settings panel.

                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setAlarm();

        Log.v("Service Info", String.valueOf(isMyServiceRunning(SendTicketNotificationService.class)));
        /*new RetreiveFeedTask().execute();*/
    }

    private void createFile() {
        String fileName = "";
        MakeCSVFile makeCSVFile = new MakeCSVFile();
        try {
            fileName = makeCSVFile.createCSVFile(amexDataAccess);
            Toast.makeText(MainActivity.this, "File exported successfully at: " + fileName + " location",
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setAlarm() {

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);


        // Set the alarm to start at 21:32 PM
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 9);

        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1 * 60 * 2, pi); // Millisec * Second * Minute
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.action_showLog) {
            FragmentManager fm = getSupportFragmentManager();
            final PinDialogFragment pinDialogFragment = new PinDialogFragment();
            pinDialogFragment.setCancelable(false);
            String mPin = amexDataAccess.getMPin();
            pinDialogFragment.setArguments(mPin);
            pinDialogFragment.setListener(new PinDialogFragment.Listener() {
                @Override
                public void showLogs() {
                    saveFile.setVisibility(View.VISIBLE);
                    pinDialogFragment.dismiss();
                    if (ticketAdapter != null) {
                        ticketAdapter.setFullDetailMod(true);
                        ticketAdapter.notifyDataSetChanged();
                    }
                }
            });
            pinDialogFragment.show(fm, "ticket");
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void smsReadPermissionDenied() {
        createAlertDialog("Amex Alerter need SMS read/send permission to " +
                "read ticket details sms and send Acceptance message .Please enable SMS permission from setting.");
    }

    private void createAlertDialog(String alerMessage) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                MainActivity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Alert Dialog");

        // Setting Dialog Message
        alertDialog.setMessage(alerMessage);

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.tick);

        // Setting OK Button
        alertDialog.setCancelable(false);
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.cancel();
                final Intent i = new Intent();
                i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setData(Uri.parse("package:" + getPackageName()));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(i);
            }
        });
        //Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void smsReadPermissionGranted() {

    }

    @Override
    public void externalStoragePermissionDenied() {
        createAlertDialog("Amex Alerter need write external storage permission to save exported csv file");
    }

    @Override
    public void externalStoragePermissionGranted() {
        createFile();
    }

    public class GetAllTicketListTask extends AsyncTask<Void, Void, List<TicketDetail>> {
        @Override
        protected List<TicketDetail> doInBackground(Void... params) {
            amexDataAccess.openDbToRead();
            Cursor c = amexDataAccess.getAllTicket();
            ticketDetailList = new ArrayList<>();
            if (c != null) {
                while (c.moveToNext()) {
                    TicketDetail ticketDetail = new TicketDetail();
                    ticketDetail.setInc(c.getString(c.getColumnIndex(TicketDetailEntity.COLUMN_TICKET_INC)));
                    ticketDetail.setQueueName(c.getString(c.getColumnIndex(TicketDetailEntity.COLUMN_TICKET_QUEUE)));
                    ticketDetail.setSeverity(c.getString(c.getColumnIndex(TicketDetailEntity.COLUMN_TICKET_SEVERITY)));
                    ticketDetail.setTicketDesc(c.getString(c.getColumnIndex(TicketDetailEntity.COLUMN_TICKET_DESC)));
                    ticketDetail.setTicketReceiveTime(c.getString(c.getColumnIndex(TicketDetailEntity.COLUMN_TICKET_RECEIVE_TIME)));
                    ticketDetail.setTicketAcceptanceTime(c.getString(c.getColumnIndex(TicketDetailEntity.COLUMN_TICKET_ACCEPTANCE_TIME)));
                    ticketDetail.setAcceptanceMessageTime(c.getString(c.getColumnIndex(TicketDetailEntity.COLUMN_ACCEPTANCE_MESSAGE_RECEIVING_TIME)));
                    ticketDetail.setAcceptanceMessageTime(c.getString(c.getColumnIndex(TicketDetailEntity.COLUMN_TICKET_ACCEPTED_BY)));
                    ticketDetailList.add(ticketDetail);
                }
            }
            return ticketDetailList;
        }

        @Override
        protected void onPostExecute(List<TicketDetail> ticketDetailList) {
            super.onPostExecute(ticketDetailList);
            if (ticketDetailList != null) {
                ticketAdapter = new TicketAdapter(ticketDetailList);
                ticketAdapter.setFullDetailMod(false);
                recyclerView.setAdapter(ticketAdapter);
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
