package com.example.lee.playinseoul;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lee.playinseoul.service.MyService;

public class TourActivity extends AppCompatActivity {
    String  title, content,  mapx,mapy;
    TextView tour;
    TextView text;
    SharedPreferences.Editor editor;
    SharedPreferences setting;
    NotificationManager Notifi_M;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        Intent intent = getIntent();
        if(intent!=null) {
            mapx=intent.getStringExtra("mapx");
            mapy=intent.getStringExtra("mapy");
            title=intent.getStringExtra("title");
        }

        tour = (TextView) findViewById(R.id.tour_button_start);
        text =(TextView) findViewById(R.id.tour_text_title) ;


        setting = getSharedPreferences("ExitSetting",0);
        editor = setting.edit();

        if(setting.getString("mapx","0") != "0") {
            text.setText("여행을 끝내시겠습니까? ");
            tour.setText("여행 중지");
        }

        tour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(setting.getString("mapx","0") != "0") {

                    editor.clear();
                    editor.commit();


                    Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notifi_M.cancel(666);
                    Intent intent2 = new Intent(TourActivity.this, MyService.class);
                    stopService(intent2);
                    Intent intent3 = new Intent(TourActivity.this, MainActivity.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent3);
                }
                else{
                    if(setting.getString("mapx","0").equals("0")) {
                        double[] xy = new double[2];
                        xy[0] = Double.valueOf(mapy).doubleValue();
                        xy[1] = Double.valueOf(mapx).doubleValue();

                        Intent intent3 = new Intent(TourActivity.this, MyService.class);
                        intent3.putExtra("xy", xy);
                        intent3.putExtra("title", title);
                        startService(intent3);

                        Intent _intent = new Intent(TourActivity.this, FindWayActivity.class);
                        _intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        _intent.putExtra("mapx", mapx);
                        _intent.putExtra("mapy", mapy);
                        _intent.putExtra("title", title);
                        startActivity(_intent);

                        TourActivity.this.setResult(1);
                        finish();
                    }
                }
            }
        });
    }
}
