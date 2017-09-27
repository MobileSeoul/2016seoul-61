package com.example.lee.playinseoul.SharePostBoard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lee.playinseoul.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by xyom on 2016-09-16.
 */
public class postMainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener{

    getServerData get;
    ArrayList<postData> allData;
    postListAdapter pAdapter=null;

    boolean lastItemVisibleFlag = false;
    private int PAGE = 1;
    private int totalPage = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        allData = new ArrayList<>();

        get = new getServerData();
        try {
            get.execute("http://52.78.92.129:8080/PlayInSeoul/getData.jsp").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        ListView postList = (ListView)findViewById(R.id.postListView);
        pAdapter = new postListAdapter(this);

        postList.setAdapter(pAdapter);
        postList.setOnItemClickListener(this);
        postList.setOnScrollListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int position = allData.size()-1-i;
        postData data = allData.get(position);
        Intent intent = new Intent(this,com.example.lee.playinseoul.StampMap.showContentActivity.class);
        intent.putExtra("request",2);
        intent.putExtra("title",data.title);
        intent.putExtra("content",data.content);
        intent.putExtra("writer",data.postWriter);
        intent.putExtra("rating",data.rating);
        intent.putExtra("mapx",data.mapx);
        intent.putExtra("mapy",data.mapy);
        intent.putExtra("image_path",data.image_path);
        intent.putExtra("date",data.date);

        startActivity(intent);

    }
    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        //스크롤이 바닥에 닿아 멈춘 상태의 처리
        if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&
                lastItemVisibleFlag){

            if(PAGE<totalPage)
            {
                PAGE++;

                int firstIndex = allData.size()-(1+(PAGE-1)*10);
                int lastIndex = firstIndex-9;

                if(PAGE==totalPage&&(allData.size()%10)!=0)
                    lastIndex = firstIndex-(allData.size()%10)+1;

                Log.d("firstIndex",firstIndex+"");
                Log.d("lastIndex",lastIndex+"");
                for(int i=firstIndex;i>=lastIndex;i--)
                {
                    pAdapter.addItem(allData.get(i));
                }
                pAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount>0) && (firstVisibleItem +visibleItemCount >= totalItemCount);
        Log.d("lastItemflag",lastItemVisibleFlag+"");
    }

    private class getServerData extends AsyncTask<String, Integer,String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            try{
                // 연결 url 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                // 연결되었으면.
                if(conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for(;;){
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if(line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch(Exception ex){
                ex.printStackTrace();
            }
            return jsonHtml.toString();

        }

        protected void onPostExecute(String str){


            try {
                JSONObject root = new JSONObject(str);
                JSONArray jarray = root.getJSONArray("List");

                for(int i=0;i<jarray.length();i++)
                {
                    JSONObject jo = jarray.getJSONObject(i);
                    postData pd = new postData();
                    pd.postNumber = jo.getInt("number");
                    pd.postWriter = jo.getString("writer");
                    pd.date=jo.getString("date");
                    pd.title=jo.getString("title");
                    pd.content=jo.getString("content");
                    pd.mapx=jo.getString("mapx");
                    pd.mapy=jo.getString("mapy");
                    pd.rating=jo.getString("rating");
                    pd.image_path=jo.getString("image_path");

                    allData.add(pd);
                }

                if((allData.size()%10)!=0)
                {
                    totalPage=allData.size()/10+1;
                }
                else
                {
                    totalPage=allData.size()/10;
                }


                //
                ///
                //최신글이 가장위로 보이게 하기 위해서 반대로 집어 넣는다.
                for(int i=allData.size()-1;i>=allData.size()-10;i--)
                {
                    pAdapter.addItem(allData.get(i));
                }
                pAdapter.notifyDataSetChanged();
                //이 작업이 끝나면 Adapter에 변화됬음을 알린다.
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    class postData
    {
        int postNumber;
        String postWriter;
        String date;
        String title;
        String content;
        String mapx;
        String mapy;
        String rating;
        String image_path;
    }

}
