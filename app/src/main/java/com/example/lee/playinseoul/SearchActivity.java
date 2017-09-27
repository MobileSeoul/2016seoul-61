package com.example.lee.playinseoul;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lee.playinseoul.adapter.TourAdapter;
import com.example.lee.playinseoul.model.Tour;
import com.example.lee.playinseoul.response.AreaUrl;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
    Spinner spinner1, spinner2;
    Button button;
    ListView listview;
    List<Tour> list;

    TourAdapter adapter;
    View footerView;
    LinearLayout footer = null;

    boolean lastItemVisibleFlag = false;
    public int PAGE = 1;

    ArrayList<Tour> tour;

    int sigu_t, tema_t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        button = (Button) findViewById(R.id.button);
        spinner1 =(Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        listview =(ListView) findViewById(R.id.listView);


        spinner1.setSelection(0);
        spinner2.setSelection(0);

        spinner1.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);
        button.setOnClickListener(this);

        listview.setOnItemClickListener(this);
        listview.setOnScrollListener(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    public void onClick(View view) {
        // 도움말을 없애주는 부분
        TextView textHelp = (TextView)findViewById(R.id.search_text_help);
        textHelp.setVisibility(GONE);

        animateButton(view);
        int selectIndex = spinner1.getSelectedItemPosition();
        Resources r = getResources();
        String[] cities = r.getStringArray(R.array.city_list);
        sigu_t = siguCangeCode(cities[selectIndex]);



        int selectIndex2 = spinner2.getSelectedItemPosition();
        String[] tema = r.getStringArray(R.array.tema_list);
        tema_t = tema2ChangeCode(tema[selectIndex2]);

        PAGE=1;

        if(selectIndex == 0 || selectIndex2 == 0){
                Toast.makeText(this, "자치구와 테마를 선택해주세요", Toast.LENGTH_SHORT).show();
            }else {
                footerView = getLayoutInflater().inflate(R.layout.list_footer, null, false);
                footer = (LinearLayout) footerView.findViewById(R.id.footerVIew);
            footer.setVisibility(GONE);

            tour = new ArrayList<>();
            AreaUrl areaUrl = new AreaUrl(PAGE, tema_t, sigu_t);
            try {
                URL url = new URL(areaUrl.getAreaUrl());
                new TourResponse(this).execute(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    // 돋보기를 45도 회전하는 애니메이션
    private void animateButton(final View view) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                view.animate().rotation(45);
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.animate().rotation(0);
            }
        }, 250);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        //스크롤이 바닥에 닿아 멈춘 상태의 처리
        if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&
                lastItemVisibleFlag){
            //현재 페이지가 전체 페이지보다 작을 경우
            //페이지 수를 1 증가시키고 다시 통신을 시도한다.
            if(PAGE <(Tour.totalCount/10)) {
                PAGE++;

                AreaUrl areaUrl = new AreaUrl(PAGE, tema_t, sigu_t);

                try {
                    URL url = new URL(areaUrl.getAreaUrl());
                    new TourResponse(this).execute(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }else{footer.setVisibility(GONE);}
        }

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount>0) && (firstVisibleItem +visibleItemCount >= totalItemCount);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private int tema2ChangeCode(String tema2) {
        if(tema2.equals("관광지")){
            return 12;
        }
        else if(tema2.equals("문화시설")){
            return 14;
        }
        else if(tema2.equals("축제 공연 행사")){
            return 15;
        }
        else if(tema2.equals("여행코스")){
            return 25;
        }
        else if(tema2.equals("레포츠")){
            return 28;
        }
        else if(tema2.equals("숙박")){
            return 32;
        }
        else if(tema2.equals("쇼핑")){
            return 38;
        }
        else if(tema2.equals("음식")){
            return 39;
        }
        return -1;
    }

    private int siguCangeCode(String sigu) {
        if(sigu.equals("강남구")){
            return 1;
        }
        else if(sigu.equals("강동구")){
            return 2;
        }
        else if(sigu.equals("강북구")){
            return 3;
        }
        else if(sigu.equals("강서구")){
            return 4;
        }
        else if(sigu.equals("관악구")){
            return 5;
        }
        else if(sigu.equals("광진구")){
            return 6;
        }
        else if(sigu.equals("구로구")){
            return 7;
        }
        else if(sigu.equals("금천구")){
            return 8;
        }
        else if(sigu.equals("노원구")){
            return 9;
        }
        else if(sigu.equals("도봉구")){
            return 10;
        }
        else if(sigu.equals("동대문구")){
            return 11;
        }
        else if(sigu.equals("동작구")){
            return 12;
        }
        else if(sigu.equals("마포구")){
            return 13;
        }
        else if(sigu.equals("서대문구")){
            return 14;
        }
        else if(sigu.equals("서초구")){
            return 15;
        }
        else if(sigu.equals("성동구")){
            return 16;
        }
        else if(sigu.equals("성북구")){
            return 17;
        }
        else if(sigu.equals("송파구")){
            return 18;
        }
        else if(sigu.equals("양천구")){
            return 19;
        }
        else if(sigu.equals("영등포구")){
            return 20;
        }
        else if(sigu.equals("용산구")){
            return 21;
        }
        else if(sigu.equals("은평구")){
            return 22;
        }
        else if(sigu.equals("종로구")){
            return 23;
        }
        else if(sigu.equals("중구")){
            return 24;
        }
        else if(sigu.equals("중랑구")){
            return 25;
        }
        return  -1;
    }
    class TourResponse extends AsyncTask<URL, Tour, ArrayList<Tour>> {

        Context context;

        public TourResponse(Context context) {
            this.context = context;
        }

        /*메소드에 의해 invoke된다.*/
        @Override
        protected void onProgressUpdate(Tour... values) {
            // super.onProgressUpdate(values);
        }

        /*쓰레드가 실행되기 이전에 메소드*/
        @Override
        protected void onPreExecute() {
            //AsyncTask 동작 중 사전에 화면에 표시할 내용이 들어간다
            //대부분 progress를 많이 사용한다 - 사용자에게 동작중이라는 것을 보여주기 위해
            super.onPreExecute();
            footerView.setVisibility(View.VISIBLE);
            listview.addFooterView(footerView);
        }

        //백그라운드 동작이 끝난 후 UI화면 또 는 Data처리를 한다.
        @Override
        protected void onPostExecute(ArrayList<Tour> tour) {
            super.onPostExecute(tour);
            if(Tour.totalCount == 0 ){
                footer.setVisibility(GONE);

                Toast.makeText(SearchActivity.this,"데이터가 존재하지 않습니다.",Toast.LENGTH_SHORT).show();
            }
            else {

                if(Tour.totalCount<10)
                    footer.setVisibility(GONE);

                list = tour;
                adapter = new TourAdapter(context, R.layout.list_item, list);

                listview.removeFooterView(footerView);
                listview.setAdapter(adapter);
                //새로 로딩되는 페이지부분부터 보여줌
                listview.setSelection(adapter.getCount() - 10);

            }
        }

        //이부분에서는 DB처리 또는 백그라운드 동작을 구현 한다
        @Override
        protected ArrayList<Tour> doInBackground(URL... params) {
            String addr1 = null;
            String firstimage = null;
            String title = null;
            String mapx = null;
            String mapy = null;
            String contentId = null;
            String contenttypeid = null;

            try {
                //웹에서 데이터를 가져옴
                InputStream is = params[0].openStream();
                //xmlpullparser인터페이스이기 때문에 클래스 생성후 xmlpullparser를 사용
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                //xml문서를 이벤트를 이용해서 데이터를 추출해주는 객체
                XmlPullParser xpp = factory.newPullParser(); // 파싱하는 객체

                //가져온 데이터를 xmlpullparser에 넣어줌
                xpp.setInput(new InputStreamReader(is, "UTF-8")); // inputstream으로부터 xml 입력받기

                String tag = "";


                xpp.next();
                //이벤트 값을 얻어옴
                int eventType = xpp.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {

                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG: // Start tag인지점을 구분하여 객체에 그 정보 값을 넣어준다.
                            tag = xpp.getName(); // 태그의 이름을 얻어온다( item, contentid 등등)
                            break;

                        case XmlPullParser.TEXT:


                            if (tag.equals("addr1")) {
                                addr1 = xpp.getText();
                                xpp.next();
                            }

                            else if (tag.equals("contenttypeid")) {
                                contenttypeid = xpp.getText();
                                xpp.next();
                            } else if (tag.equals("contentid")) {
                                contentId = xpp.getText();
                                xpp.next();
                            } else if (tag.equals("firstimage")) {
                                firstimage = xpp.getText();
                                xpp.next();
                            }

                            else if (tag.equals("mapx")) {
                                mapx = xpp.getText();
                                xpp.next();
                            } else if (tag.equals("mapy")) {
                                mapy = xpp.getText();
                                xpp.next();
                            }
                            else if (tag.equals("title")) {
                                title = (xpp.getText());
                                xpp.next();
                            } else if (tag.equals("totalCount")) {
                                Tour.totalCount = Integer.parseInt(xpp.getText());
                                xpp.next();
                            }
                            break;

                        case XmlPullParser.END_TAG: // end tag 일때
                            tag = xpp.getName();
                            if (tag.equals("item")) {

                                if(title == null){
                                    tour.add(new Tour());
                                }
                                else {
                                    tour.add(new Tour(addr1, firstimage, title, mapx, mapy, contentId, contenttypeid));

                                    addr1 = null;
                                    firstimage = null;
                                    title = null;
                                    mapx = null;
                                    mapy = null;
                                    contentId = null;
                                    contenttypeid = null;
                                }
                            }
                            break;

                    } // switch 끝

                    //다음 이벤트로 이동.
                    eventType = xpp.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return tour;
        }

    }
}
