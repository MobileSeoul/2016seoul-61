package com.example.lee.playinseoul.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.lee.playinseoul.R;
import com.example.lee.playinseoul.Visitorlist;

public class MyService2 extends Service {
    NotificationManager Notifi_M;
    ServiceThread2 thread;
    Notification Notifi ;
    double[] xy = new  double[2];
    String title;

    public MyService2() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onDestroy() {
        thread.stopForever();
        thread = null;//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id3 " + startId + ": " + intent);

        xy = intent.getDoubleArrayExtra("xy");
        title = intent.getStringExtra("title");

        Intent intent2 = new Intent(MyService2.this, MyService.class);
        stopService(intent2);

        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread2(handler);
        thread.start();

        return START_NOT_STICKY;
    }

    class myServiceHandler extends Handler {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void handleMessage(android.os.Message msg) {

            Intent intent2 = new Intent(MyService2.this, Visitorlist.class);
            intent2.putExtra("xy",xy);
            intent2.putExtra("title",title);
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyService2.this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

            Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Notifi = new Notification.Builder(getApplicationContext())
                    .setOngoing(true)
                    .setContentTitle("방명록")
                    .setContentText("다녀간 곳의 발자취를 남겨보세요!!")
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_circle_app))
                    .setSmallIcon(R.drawable.icon_circle_app)
                    .setTicker("알림!!!")
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_MAX)
                    .build();

            Notifi.defaults = Notification.DEFAULT_SOUND;


            Notifi.flags = Notification.FLAG_ONLY_ALERT_ONCE;

            Notifi.flags = Notification.FLAG_ONGOING_EVENT;


            Notifi_M.notify(777, Notifi);

            Toast.makeText(MyService2.this, "목적지에 도착하였습니다.", Toast.LENGTH_LONG).show();

            stopSelfResult(1);
        }
    }
}

