package com.example.lee.playinseoul.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.lee.playinseoul.FindWayActivity;
import com.example.lee.playinseoul.R;
import com.example.lee.playinseoul.Visitorlist;

public class MyService extends Service {
    NotificationManager Notifi_M;
    ServiceThread thread;
    Notification Notifi ;
    double meter;
    Location myLocation;
    Location destination;
    LocationManager manager;
    GPSListener gpsListener;
    double[] xy = new  double[2];
    String title;
    boolean a;

    PendingIntent pintent2;

    SharedPreferences setting;
    SharedPreferences.Editor editor;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("LocalService", "Received start!!");
        a= true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
            xy = intent.getDoubleArrayExtra("xy");
            title = intent.getStringExtra("title");


            myServiceHandler handler = new myServiceHandler();
            thread = new ServiceThread(handler);
            thread.start();
            Log.i("Local","onstart");

        return START_REDELIVER_INTENT;
    }

    public void onDestroy() {
        Log.i("LocalService", "Received ENd!!");

        manager.removeProximityAlert(pintent2);
        manager.removeUpdates(gpsListener);
        manager =null ;
        gpsListener =null;
        thread.stopForever();
        thread = null;//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.

        if (ActivityCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }


    class myServiceHandler extends Handler {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(android.os.Message msg) {
            /*****중지 알림 실행*****/
            if(a){
                Intent intent2 = new Intent(MyService.this, FindWayActivity.class);
                intent2.putExtra("mapx",xy[1]+"");
                intent2.putExtra("mapy",xy[0]+"");
                intent2.putExtra("title",title);
                PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

                Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                // set listener
                manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                gpsListener = new GPSListener();


                if(xy[0] == -1 || xy[1] == -1){
                    Notifi = new Notification.Builder(getApplicationContext())
                            .setOngoing(true)
                            .setContentTitle("목적지 탐색 불가")
                            .setContentText("위도/경도 값이 없어 목적지 탐색 불가")
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_circle_app))
                            .setSmallIcon(R.drawable.icon_traveling)
                            .setTicker("알림!!!")
                            .setContentIntent(pendingIntent)
                            .build();

                    Notifi.flags = Notification.FLAG_ONGOING_EVENT;
                    Notifi.defaults = Notification.DEFAULT_SOUND;
                    Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;

                    Notifi_M.notify(666, Notifi);
                    stopSelf();
                }else {
                    Notifi = new Notification.Builder(getApplicationContext())
                            .setOngoing(true)
                            .setContentTitle("여행중..")
                            .setContentText("여행을 중단하시려면 클릭하세요")
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_circle_app))
                            .setSmallIcon(R.drawable.icon_traveling)
                            .setTicker("알림!!!")
                            .setContentIntent(pendingIntent)
                            .build();

                    Notifi.flags = Notification.FLAG_ONGOING_EVENT;
                    Notifi.defaults = Notification.DEFAULT_SOUND;
                    Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;

                    Notifi_M.notify(666, Notifi);
                }

                intent2 = new Intent(MyService.this, Visitorlist.class);
                intent2.putExtra("xy",xy);
                intent2.putExtra("title",title);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                pintent2 = PendingIntent.getActivity(MyService.this, 0, intent2, PendingIntent.FLAG_ONE_SHOT);

                String mapy = xy[0]+"";
                String mapx = xy[1]+"";

                setting = getSharedPreferences("ExitSetting",0);
                editor = setting.edit();

                editor.putString("mapx",mapx);
                editor.putString("mapy",mapy);
                editor.putString("title",title);
                editor.putString("stop","stop");
                editor.commit();


                myLocation = new Location("Point A");
                destination = new Location("point B");

                destination.setLatitude(xy[0]);
                destination.setLongitude(xy[1]);

                a=false;
            }

            if (ActivityCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(MyService.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Log.i("LocalSer","된다.");



            long minTime = 5000;
            float minDistance = 0;

            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener);

            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener);


            if(meter < 80 && meter > -40) {
                Log.i("GPSLocation :",meter+"");
                manager.addProximityAlert(xy[0], xy[1], 20, 5000, pintent2);
            }

           //manager.addProximityAlert(37.5450795, 126.8389698, 100, 20000, pintent2);
        }
    }
   class GPSListener implements LocationListener {

        public void onLocationChanged(Location location) {
            double latitude;
            double longitude;
            //capture location data sent by current provider
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            myLocation.setLatitude(latitude);
            myLocation.setLongitude(longitude);

            meter = myLocation.distanceTo(destination);

            String msg = "Latitude : "+ latitude + "\nLongitude:"+ longitude + "\nsub:" + meter;
            Log.i("GPSLocationService", msg);

        }

        public void onProviderDisabled(String provider) {}

        public void onProviderEnabled(String provider) {}

        public void onStatusChanged(String provider, int status, Bundle extras) {}

    }

}
