package com.example.lee.playinseoul;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.lee.playinseoul.model.LightTextView;

import java.util.ArrayList;
import java.util.Random;

public class RandomActivity extends AppCompatActivity {
    // 슬롯 애니메이션 플립퍼
    ViewFlipper guFlipper, temaFlipper, nameFlipper;

    // 버튼
    TextView button_restart, button_detail;

    // 슬롯 핸들러
    Handler handler;

    // 슬롯 애니메이션 속도 저장 변수
    long slot_speed;

    // 자치구와 테마
    String[] gu = {"강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", "노원구", "도봉구", "동대문구" ,"동작구", "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구", "양천구" ,"영등포구", "용산구 ","은평구 ","종로구","중구","중랑구"};
    String[] tema = {"관광지", "문화시설", "축제공연행사", "여행코스", "레포츠", "숙박", "쇼핑", "음식"};

    // 여행지 데이터 리스트
    ArrayList<dataSet> dataSets;

    // 랜덤 자치구, 테마, 여행지
    int gu_number, tema_number, name_nubmer;

    // 애니메이션 상태
    boolean animate = false;

    // 진행 다이얼로그
    ProgressDialog progressDialog;

    // 최대 랜덤 횟수 제한
    int count_random = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);
        handler = new Handler();

        guFlipper = (ViewFlipper)findViewById(R.id.slot_flipper_gu);
        temaFlipper = (ViewFlipper)findViewById(R.id.slot_flipper_tema);
        nameFlipper = (ViewFlipper)findViewById(R.id.slot_flipper_name);

        // 구 목록들을 플립퍼에 추가시켜줌
        for(int i=0; i<gu.length; i++) {
            AddTextView(guFlipper,gu[i]);
        }
        // 테마 목록들을 플립퍼에 추가시켜줌
        for(int i=0; i<tema.length; i++) {
            AddTextView(temaFlipper, tema[i]);
        }
        button_restart = (TextView) findViewById(R.id.slot_button_restart);
        button_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView btn = (TextView) v;
                btn.setText("다시 시작");
                StartAnimation();
            }
        });
        button_detail = (TextView) findViewById(R.id.slot_button_detail);
        button_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDetail();
            }
        });

        handler = new Handler();
    }

    // 뷰 그룹에 텍스트뷰를 추가 시켜주는 함수
    private void AddTextView(ViewGroup viewGroup, String str) {
        // 텍스트 뷰로 추가시켜줄거임
        LightTextView tv = new LightTextView(getApplicationContext());
        // 텍스트 컬러는 검정
        tv.setTextColor(Color.parseColor("#ffffff"));
        // 텍스트는 미리 저장되어있는 배열에서 가져옴
        tv.setText(str);
        // 글자를 가운데에 배치할거임
        tv.setGravity(Gravity.CENTER);
        // 높이와 폭은 match_parent로 해서 추가시켜줌
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        viewGroup.addView(tv,params);
    }

    // 자세히 보기 액티비티로 전환하는 함수
    private void ShowDetail() {
        Intent intent = new Intent(RandomActivity.this, DetailActivity.class);
        intent.putExtra("contentid", dataSets.get(name_nubmer).contentid);
        intent.putExtra("imageurl", dataSets.get(name_nubmer).firstimage);
        intent.putExtra("title", dataSets.get(name_nubmer).title);
        intent.putExtra("mapx", dataSets.get(name_nubmer).mapx);
        intent.putExtra("mapy", dataSets.get(name_nubmer).mapy);
        intent.putExtra("contentType",tema_number+"");
        startActivity(intent);
    }

    // 전체적인 슬롯 애니메이션을 실행시켜주는 함수
    private void StartAnimation() {
        // 애니메이션 중이면 무시함
        if(!animate) {
            progressDialog = ProgressDialog.show(RandomActivity.this, "", "잠시만 기다려주세요.");
            button_detail.setVisibility(View.INVISIBLE);
            button_restart.setEnabled(false);
            nameFlipper.removeAllViews();
            MakeRandomList();
        } else {
            Toast.makeText(RandomActivity.this, "이미 선택중입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void MakeRandomList() {
        if(count_random >= 10) {
            if(progressDialog!=null) progressDialog.dismiss();
            Toast.makeText(RandomActivity.this, "데이터를 가져오는데 어려움이 있습니다. 잠시 후에 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            count_random++;
            // 랜덤 객체 생성
            Random rand = new Random();
            // 구와 테마를 랜덤으로 지정해줌
            gu_number = rand.nextInt(gu.length);
            tema_number = rand.nextInt(tema.length);
            // 구와 테마에 맞는 여행지들을 불러옴
            int code_tema = GetTemaCode(tema[tema_number]);

            GetDataSets(gu_number + 1, code_tema);
        }
    }

    private void GetDataSets(final int code_gu, final int code_tema) {
        dataSets = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                getInformation g = new getInformation();
                String str_total = g.getTotalNum(code_tema, code_gu, 1);
                int totalNum = 0;
                if(!str_total.equals("")) totalNum = Integer.parseInt(str_total);

                    ArrayList<dataSet> result = g.getXmlData(code_tema, code_gu, 1,totalNum);
                    for(int j=0; j<result.size(); j++) dataSets.add(result.get(j));
                // 데이터가 존재하면 애니메이션을 시작함
                Log.v("dataSets size", dataSets.size() + "");
                if(dataSets.size()>0) {
                    slot_handler.sendEmptyMessage(0);
                }
                // 데이터가 존재하지 않으면 처음부터 다시 선정함
                else {
                    slot_handler.sendEmptyMessage(1);
                }
            }
        }).start();
    }

    Handler slot_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0) {
                progressDialog.dismiss();
                name_nubmer = new Random().nextInt(dataSets.size());
                StartGuSlot();
            } else {
                MakeRandomList();
            }
        }
    };

    private void StartGuSlot() {
        animate = true;
        int start_number = gu_number + flip_size%gu.length;
        start_number %= gu.length;
        guFlipper.setDisplayedChild(start_number);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                int count=flip_size;
                slot_speed = 50;
                while(count>0) {
                    count--;
                    ChangeSlotSpeed(count);
                    handler.post(GuUp);
                    try {
                        Thread.sleep(slot_speed+5);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        StartTemaSlot();
                    }
                });
            }
        });
        t1.start();
    }
    private void StartTemaSlot() {
        animate = true;
        int start_number = tema_number + flip_size%tema.length;
        start_number %= tema.length;
        temaFlipper.setDisplayedChild(start_number);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                int count=flip_size;
                slot_speed = 100;
                while(count>0) {
                    count--;
                    ChangeSlotSpeed(count);
                    handler.post(TemaUp);
                    try {
                        Thread.sleep(slot_speed+5);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < dataSets.size(); i++) {
                            AddTextView(nameFlipper, dataSets.get(i).title);
                        }
                        StartNameSlot();
                    }
                });
            }
        });
        t1.start();
    }
    private void StartNameSlot() {
        animate = true;
        int start_number = name_nubmer + flip_size%dataSets.size();
        start_number %= dataSets.size();
        nameFlipper.setDisplayedChild(start_number);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                int count=flip_size;
                slot_speed = 150;
                while(count>0) {
                    count--;
                    ChangeSlotSpeed(count);
                    handler.post(NameUp);
                    try {
                        Thread.sleep(slot_speed+5);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                animate= false;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        button_detail.setVisibility(View.VISIBLE);
                        button_restart.setEnabled(true);
                    }
                });
            }
        });
        t1.start();
    }

    // 슬롯 애니메이션을 구현해주는 Runnable 들임
    private Runnable GuUp = new Runnable() {
        @Override
        public void run() {
            Animation inFromTop = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, -1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            Animation outToBottom = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 1.0f);
            inFromTop.setInterpolator(new AccelerateInterpolator());
            inFromTop.setDuration(slot_speed);
            outToBottom.setInterpolator(new AccelerateInterpolator());
            outToBottom.setDuration(slot_speed);
            guFlipper.setInAnimation(inFromTop);
            guFlipper.setOutAnimation(outToBottom);
            guFlipper.showPrevious();
        }
    };
    private Runnable TemaUp = new Runnable() {
        @Override
        public void run() {
            Animation inFromTop = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, -1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            Animation outToBottom = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 1.0f);
            inFromTop.setInterpolator(new AccelerateInterpolator());
            inFromTop.setDuration(slot_speed);
            outToBottom.setInterpolator(new AccelerateInterpolator());
            outToBottom.setDuration(slot_speed);
            temaFlipper.setInAnimation(inFromTop);
            temaFlipper.setOutAnimation(outToBottom);
            temaFlipper.showPrevious();
        }
    };
    private Runnable NameUp = new Runnable() {
        @Override
        public void run() {
            Animation inFromTop = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, -1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            Animation outToBottom = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 1.0f);
            inFromTop.setInterpolator(new AccelerateInterpolator());
            inFromTop.setDuration(slot_speed);
            outToBottom.setInterpolator(new AccelerateInterpolator());
            outToBottom.setDuration(slot_speed);
            nameFlipper.setInAnimation(inFromTop);
            nameFlipper.setOutAnimation(outToBottom);
            nameFlipper.showPrevious();
        }
    };

    // 슬롯 애니메이션 회전 횟수
    private final int flip_size = 20;
    private void ChangeSlotSpeed(int count) {
        if(count>10) slot_speed = 100;
        else if(count>5) slot_speed = 200;
        else if(count>2) slot_speed = 300;
        else slot_speed = 450;
    }

    private int GetTemaCode(String tema) {
        switch (tema) {
            case "관광지":
                return 12;
            case "문화시설":
                return 14;
            case "축제 공연 행사":
                return 15;
            case "여행코스":
                return 25;
            case "레포츠":
                return 28;
            case "숙박":
                return 32;
            case "쇼핑":
                return 38;
            case "음식":
                return 39;
        }
        return 0;
    }
}