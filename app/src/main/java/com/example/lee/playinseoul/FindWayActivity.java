package com.example.lee.playinseoul;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lee.playinseoul.service.MyService;
import com.example.lee.playinseoul.service.MyService2;

import java.nio.charset.Charset;
import java.util.List;

public class FindWayActivity extends AppCompatActivity {

    SharedPreferences setting;
    SharedPreferences.Editor editor;
    NotificationManager Notifi_M;

    String baseURL = "http://map.daum.net/link/to/";
    String title = "카카오";
    String mapx="127.108212";
    String mapy="37.402056";
    WebView mWebview;

    TextView tourText=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findwaylayout);

        setting = getSharedPreferences("ExitSetting",0);
        editor= setting.edit();

        Intent intent = getIntent();

        tourText = (TextView)findViewById(R.id.btnTour_Start);

        mapx=intent.getStringExtra("mapx");
        mapy=intent.getStringExtra("mapy");
        title=intent.getStringExtra("title");

        if(mapx == null || mapy == null){
            mapx="-1"; mapy="-1";
        }

        Log.i("[TEST]",mapx+" / "+mapy +" / "+ title);

        checkGPS();
        //dNmae=
        //mapx=
        //mapy=
        mWebview = (WebView)findViewById(R.id.webView);

        WebSettings webSettings = mWebview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setGeolocationEnabled(true);
            webSettings.setSupportMultipleWindows(true); // This forces ChromeClient enabled.

            mWebview.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onReceivedTitle(WebView view, String title) {
                    getWindow().setTitle(title); //Set Activity tile to page title.
                }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin,true,false);
            }

        });

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

        });


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }

        title = title.replaceAll("\\u0000","");
        mapx = mapx.replaceAll("\\u0000","");
        mapy = mapy.replaceAll("\\u0000","");
        mapy= mapy.replaceAll("\\t","");

        Log.d("url",baseURL+title+","+mapy+","+mapx);
        mWebview.loadUrl(baseURL+title+","+mapy+","+mapx);

        tourText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FindWayActivity.this,TourActivity.class);
                intent.putExtra("mapx",mapx);
                intent.putExtra("mapy",mapy);
                intent.putExtra("title",title);

                startActivityForResult(intent,1);
            }
        });


        //****************위치오면 알림******************

        if(setting.getString("stop","0").equals("stop")){

            tourText.setText("여행 중지");

            tourText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(FindWayActivity.this,TourActivity.class);
                    intent.putExtra("mapx",mapx);
                    intent.putExtra("mapy",mapy);
                    intent.putExtra("title",title);

                    startActivity(intent);

                }
            });
            /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("여행을 종료하시겠습니까?!")
                    .setCancelable(false)
                    .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("네", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            editor.clear();
                            editor.commit();

                            Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            Notifi_M.cancel(666);
                            Intent intent2 = new Intent(FindWayActivity.this, MyService.class);
                            stopService(intent2);
                            Intent intent3 = new Intent(FindWayActivity.this, MainActivity.class);
                            intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent3);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();*/
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if((keyCode==KeyEvent.KEYCODE_BACK)&&mWebview.canGoBack())
        {
            mWebview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==0) {
            mWebview.loadUrl(baseURL + title + "," + mapy + "," + mapx);
            Toast.makeText(FindWayActivity.this, "reloaded", Toast.LENGTH_LONG).show();
        }
        else if (requestCode==1&&resultCode==1)
        {
            tourText.setText("여행 중지");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean checkGPS() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPS = lm.isProviderEnabled (LocationManager.GPS_PROVIDER);
        if(isGPS) {
            return true;
        }
        else {
            Toast.makeText(FindWayActivity.this, "GPS 사용을 체크해주세요.", Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
        }
        return false;
    }
}
