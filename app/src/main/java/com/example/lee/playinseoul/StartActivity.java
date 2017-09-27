package com.example.lee.playinseoul;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StartActivity extends AppCompatActivity {

    SharedPreferences setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getSharedPreferences("PlayInSeoul", 0);
        setting = getSharedPreferences("ExitSetting",0);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(setting.getString("mapx","0").equals("0")) {
                    Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(StartActivity.this, FindWayActivity.class);
                    String title = setting.getString("title","");
                    String mapx = setting.getString("mapx","0");
                    String mapy = setting.getString("mapy","0");

                    intent.putExtra("mapx",mapx);
                    intent.putExtra("mapy",mapy);
                    intent.putExtra("title",title);
                    startActivity(intent);
                }
                finish();
            }
        }, 1500);
    }
}
