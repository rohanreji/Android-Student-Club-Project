package com.sensomate.travelr;

/**
 * Created by rohan on 26/3/15.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class MyService extends Service {
    int rt=0;
    private final static String TAG = "LocationLoggerService";
    LocationManager lm;
    GPSTracker writer;
    public int g;
    public int o;
    List<String> doing = new ArrayList<String>();
    public double latitude;
    public double longitude;
    String LAC1,MCC,MNC,CellID;
    MediaPlayer mp;
    public double km;
    Timer timer;
    GPSTracker gps;
    String[] tokens=new String[2000];
    public MyService() {
        doing.add(" ");
        g=1;
        timer=new Timer();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {

        mp = new MediaPlayer();

        gps = new GPSTracker(MyService.this);
        timer.schedule( new Task1(), 2000);
        rt=0;
    }
    public void onDestroy(){
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
    public class Task1 extends TimerTask {

        public void run() {

            Log.d("ee","cascas");
            rt++;
            if(gps.canGetLocation()){
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                final DatabaseHandler db = new DatabaseHandler(MyService.this);
                int y=0;
                List<Notes> contacts = db.getAllContacts();
                for (Notes cn : contacts) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    final Notes me=cn;
                    km=distance(cn.getLatitude(),cn.getLongitude(),latitude,longitude);
                    if(km<0.1){
                        o=0;
                        if(doing.contains(cn.getName()))
                        {
                            o=1;
                        }
                        if(o==0)
                        {
                            tokens = cn.getName().split("_");
                            Intent intent = new Intent(MyService.this, MainActivity.class);
                            PendingIntent pIntent = PendingIntent.getActivity(MyService.this, 0, intent, 0);
                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Notification noti = new Notification.Builder(MyService.this)
                                    .setContentTitle("You Have a Check-in")
                                    .setContentText(tokens[0]).setSmallIcon(R.drawable.ic_launcher)
                                    .setContentIntent(pIntent)
                                    .setSound(alarmSound)
                                    .getNotification();

                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            noti.flags |= Notification.FLAG_AUTO_CANCEL;
                            notificationManager.notify(0, noti);
                            doing.add(cn.getName());
                            y=1;
                        }
                    }
                }
            }
            timer.schedule( new Task1(), 2000);
        }
    }
}
