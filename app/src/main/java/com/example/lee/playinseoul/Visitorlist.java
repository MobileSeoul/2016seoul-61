package com.example.lee.playinseoul;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.lee.playinseoul.SharePostBoard.postMainActivity;
import com.example.lee.playinseoul.service.MyService;


public class Visitorlist extends AppCompatActivity {

   SharedPreferences setting;
    SharedPreferences.Editor editor;

    String baseURL;
    WebView mWebview;
    double xy[];
    String title;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitorlist);

        mWebview = (WebView)findViewById(R.id.webView2);

        mWebview.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                getWindow().setTitle(title); //Set Activity tile to page title.
            }
        });




        Vibrator vibrator =(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("목적지에 도착하였습니다. 방명록을 남겨보세요!!")
                .setCancelable(false)
                .setPositiveButton("메인화면", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(getApplicationContext(), " 사진으로 가는 페이지가 뭐지?"  , Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Visitorlist.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("방명록", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

        SharedPreferences pref = getSharedPreferences("PlayInSeoul", 0);
        id = pref.getString("login_userid", "");

        setting = getSharedPreferences("ExitSetting",0);
        editor = setting.edit();

        editor.clear();
        editor.commit();

        NotificationManager nm =
                (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(777);
        nm.cancel(666);
        nm = null;

        xy = new double[2];
        Intent intent = getIntent();
        xy = intent.getDoubleArrayExtra("xy");
        title = intent.getStringExtra("title");

        Intent intent2 = new Intent(Visitorlist.this, MyService.class);
        stopService(intent2);

        double mapy = xy[0];
        double mapx = xy[1];





        WebSettings webSettings = mWebview.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setBuiltInZoomControls(true); // 안드로이드에서 제공하는 줌 아이콘을 사용할 수 있도록 설정
        //webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND); // 플러그인을 사용할 수 있도록 설정

        //baseURL ="http://52.78.92.129:8080/Visitor/list.jsp?id="+id+"&mapx=126.8389698&mapy=37.5450795&destination=집";
        baseURL ="http://52.78.92.129:8080/Visitor/list.jsp?mapx="+mapx+"&mapy="+mapy+"&destination="+title+"&id="+id;
        mWebview.loadUrl(baseURL);


        mWebview.setWebViewClient(new MapWebViewClient());

        mWebview.goBack();//뒤로가기
        mWebview.goForward();//앞으로가기
        mWebview.reload();//새로고침

        mWebview.zoomIn();
        mWebview.zoomOut();
    }

     class MapWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
