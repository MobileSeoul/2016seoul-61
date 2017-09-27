package com.example.lee.playinseoul;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.lee.playinseoul.adapter.TourAdapter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by xyom on 2016-08-15.
 */
public class BigImageActivity extends Activity{

    ViewPager pager;
    int contentid;
    int contentType;
    int request;

    ProgressDialog progressDialog;

    ArrayList<String> result=null; // 이미지 url의 스트링객체를 가지고 있는 배열.
    Bitmap[] bitmapps = null; // 다운받은 bitmap객체를 저장하는 배열

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bigimage);


        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        int width = (int) (display.getWidth() * 1.0); //Display 사이즈의 70%

        int height = (int) (display.getHeight() * 0.7);  //Display 사이즈의 90%

        getWindow().getAttributes().width = width;

        getWindow().getAttributes().height = height;

        ////
        //// 여기까지 화면의 크기를 초절한다.
        ////


        Intent intent = getIntent();
        if(intent!=null) {
            contentid = Integer.parseInt(intent.getStringExtra("contentid"));
            contentType = Integer.parseInt(intent.getStringExtra("contentType"));
            request=intent.getIntExtra("request",0);
        }

        pager=(ViewPager)findViewById(R.id.pager);


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                getInformation g = new getInformation();
                result = g.getMoreImage(contentType,contentid);
            }
        });
        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        bitmapps = new Bitmap[result.size()]; // size만큼 할당.



        for(int i=0;i<result.size();i++)
        {
            getBitmap(i);
        }

        switch (request)
        {
            case 1:
                TourAdapter.mprogressDialog.dismiss();
                break;
            case 2:
                DetailActivity.mprogressDialog.dismiss();
                break;
        }

        CustomAdapter adapter = new CustomAdapter(getLayoutInflater(),bitmapps);

        pager.setAdapter(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    void getBitmap(final int position)
    {

        Thread mThread =new Thread(){
            public void run()
            {
                try {
                    URL url = new URL(result.get(position));

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmapps[position] = BitmapFactory.decodeStream(is);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };
        mThread.start();

        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}

class CustomAdapter extends PagerAdapter {

    LayoutInflater inflater;
    Bitmap[] bitmaps = null;

    public CustomAdapter(LayoutInflater inflater,Bitmap[] bitmaps) {
        // TODO Auto-generated constructor stub

        //전달 받은 LayoutInflater를 멤버변수로 전달
        this.inflater=inflater;
        this.bitmaps=bitmaps;
    }

    //PagerAdapter가 가지고 잇는 View의 개수를 리턴
    //보통 보여줘야하는 이미지 배열 데이터의 길이를 리턴
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return bitmaps.length; //이미지 개수 리턴(그림이 10개라서 10을 리턴)
    }

    //ViewPager가 현재 보여질 Item(View객체)를 생성할 필요가 있는 때 자동으로 호출
    //쉽게 말해, 스크롤을 통해 현재 보여져야 하는 View를 만들어냄.
    //첫번째 파라미터 : ViewPager
    //두번째 파라미터 : ViewPager가 보여줄 View의 위치(가장 처음부터 0,1,2,3...)
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO Auto-generated method stub

        View view=null;

        Log.d("position",position+"");
        //새로운 View 객체를 Layoutinflater를 이용해서 생성
        //만들어질 View의 설계는 res폴더>>layout폴더>>viewpater_childview.xml 레이아웃 파일 사용
        view= inflater.inflate(R.layout.viewpager_childview, null);

        //만들어진 View안에 있는 ImageView 객체 참조
        //위에서 inflated 되어 만들어진 view로부터 findViewById()를 해야 하는 것에 주의.
        ImageView img= (ImageView)view.findViewById(R.id.img_viewpager_childimage);

        //ImageView에 현재 position 번째에 해당하는 이미지를 보여주기 위한 작업
        //현재 position에 해당하는 이미지를 setting

            img.setImageBitmap(bitmaps[position]);

            //ViewPager에 만들어 낸 View 추가
            container.addView(view);

        //Image가 세팅된 View를 리턴
        return view;
    }

    //화면에 보이지 않은 View는파쾨를 해서 메모리를 관리함.
    //첫번째 파라미터 : ViewPager
    //두번째 파라미터 : 파괴될 View의 인덱스(가장 처음부터 0,1,2,3...)
    //세번째 파라미터 : 파괴될 객체(더 이상 보이지 않은 View 객체)
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub

        //ViewPager에서 보이지 않는 View는 제거
        //세번째 파라미터가 View 객체 이지만 데이터 타입이 Object여서 형변환 실시
        container.removeView((View)object);

    }

    //instantiateItem() 메소드에서 리턴된 Ojbect가 View가  맞는지 확인하는 메소드
    @Override
    public boolean isViewFromObject(View v, Object obj) {
        // TODO Auto-generated method stub
        return v==obj;
    }

}
