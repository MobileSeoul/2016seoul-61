package com.example.lee.playinseoul;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.lee.playinseoul.SharePostBoard.postMainActivity;
import com.example.lee.playinseoul.StampMap.*;
import com.example.lee.playinseoul.service.MyService;

public class MainActivity extends AppCompatActivity {
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //앱 초기에 위치 허용, 저장소 쓰기 읽기 허용을 요청한다.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},2);
        }

        Intent intent = getIntent();
        if(intent != null) {
            userid = intent.getStringExtra("userid");
        }

        ImageView btnPost = (ImageView) findViewById(R.id.main_button_recommend);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,postMainActivity.class);
                startActivity(intent);
            }
        });

        ImageView btnCondition = (ImageView)findViewById(R.id.main_button_condition);
        btnCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 조건 검색 액티비티 전환
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        ImageView btnRandom = (ImageView)findViewById(R.id.main_button_random);
        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 랜덤 검색 액티비티 전환
                Intent intent = new Intent(MainActivity.this, RandomActivity.class);
                startActivity(intent);
            }
        });
        ImageView btnStamp = (ImageView)findViewById(R.id.main_button_stamp);
        btnStamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //스탬프북 액티비티 전환

                Intent intent = new Intent(MainActivity.this,mapActivity.class);
                startActivity(intent);
            }
        });
        ImageView btnInform = (ImageView)findViewById(R.id.main_button_my_inform);
        btnInform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyInformActivity.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        });
    }
}
