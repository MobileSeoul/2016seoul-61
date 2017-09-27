package com.example.lee.playinseoul.StampMap;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lee.playinseoul.MainActivity;
import com.example.lee.playinseoul.R;
import com.example.lee.playinseoul.SQLManager;
import com.example.lee.playinseoul.SharePostBoard.imageRotater;
import com.example.lee.playinseoul.model.StampIconView;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class mapActivity extends AppCompatActivity implements MapView.POIItemEventListener{

    MapView mapView;
    Context mContext;
    GPSTracker gps; // gps로 현재위치를 받아오기 위한 객체.

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    ActionBarDrawerToggle drawerToggle;

    double latitude=0;
    double longitude=0;

    int isDrawerOpen=0;

    DrawerListAdapter dAdapter;
    MapPOIItem tempPoitem;
    //Fab 버튼 부분
    private Boolean isFabOpen=false;
    private FloatingActionButton fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;//펩버튼 애니메이션


    //db부분
    SQLManager sqlm;
    ArrayList<SQLManager.Stamp> results;


    //말풍선의 이벤트를 처리하기위한 커스텀 아답타
    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter{

        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.map_custom_callout_balloon, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            StampIconView iconView = (StampIconView)mCalloutBalloon.findViewById(R.id.badge);
            int color;
            float floatRating = Float.parseFloat(results.get(poiItem.getTag()).rating);
            if(floatRating >= 4) color = Color.parseColor("#7667B2");
            else if (floatRating >= 3) color = Color.parseColor("#9178C1");
            else if (floatRating >= 2) color = Color.parseColor("#AE90DD");
            else if (floatRating >= 1) color = Color.parseColor("#BDAAE0");
            else  color = Color.parseColor("#D3CAE8");
            iconView.setColor(color);

            ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText(poiItem.getItemName());
            ((TextView) mCalloutBalloon.findViewById(R.id.balloon_rating)).setText("평점 " + results.get(poiItem.getTag()).rating);

            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);


        sqlm = new SQLManager(this,"markerInfo.db",null,1);
        ///////////////여기부터 네비게이션 드로워 부분.///////////////////////////

        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList=(ListView)findViewById(R.id.left_drawer);

        dAdapter = new DrawerListAdapter(this);
        mDrawerList.setAdapter(dAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                double lati,longi;
                int position =(int)l;
                lati = Double.parseDouble(results.get(position).mapx);
                longi= Double.parseDouble(results.get(position).mapy);
                
                mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lati,longi),1,true);
            }
        });
        mDrawerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) { // 여기는 포지션이 1 2 3
                final CharSequence[] items = {"공유하기","수정하기","삭제하기"};
                final int select = (int)l;


                AlertDialog.Builder alt = new AlertDialog.Builder(mapActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                alt.setTitle("선택");
                alt.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { // 이거는 포지션 0  1 2
                        switch(i)
                        {
                            case 0: // 공유하기

                                AlertDialog.Builder alt = new AlertDialog.Builder(mapActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                                alt.setTitle("공유하시겠습니까?");
                                alt.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String title = results.get(select).title;
                                        String content = results.get(select).content;
                                        String mapx = results.get(select).mapx;
                                        String mapy = results.get(select).mapy;
                                        String image_path = results.get(select).image_path;
                                        String rating = results.get(select).rating;
                                        try {
                                            Cursor c = getContentResolver().query(Uri.parse(image_path), null, null, null, null);
                                            c.moveToNext();
                                            final String absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));

                                            mAsyncTask task = new mAsyncTask(title, content, mapx, mapy, absolutePath, rating);
                                            task.execute();
                                            Toast.makeText(mapActivity.this, "공유 되었습니다.", Toast.LENGTH_SHORT).show();
                                        }catch(Exception e )
                                        {
                                            e.printStackTrace();
                                            Toast.makeText(mapActivity.this, "공유할 사진이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                alt.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                                AlertDialog alert = alt.create();
                                alert.show();

                                break;
                            case 1: // 수정하기
                                Intent intent = new Intent(mapActivity.this,writeContentActivity.class);
                                intent.putExtra("title",results.get(select).title);
                                intent.putExtra("content",results.get(select).content);
                                intent.putExtra("rating",results.get(select).rating);
                                intent.putExtra("image_path",results.get(select).image_path);
                                intent.putExtra("request",2);
                                startActivity(intent);
                                break;
                            case 2: // 삭제하기

                                AlertDialog.Builder alts = new AlertDialog.Builder(mapActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                                alts.setTitle("삭제하시겠습니까?");
                                alts.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        sqlm.Delete(results.get(select).title);
                                        results.remove(select);
                                        dAdapter.removeItem(select);
                                        dAdapter.notifyDataSetChanged();
                                        MapPOIItem poiItems[] = mapView.getPOIItems();
                                        mapView.removePOIItem(poiItems[select]);
                                    }
                                });
                                alts.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                                AlertDialog alerts = alts.create();
                                alerts.show();

                                break;
                        }
                    }
                });

                AlertDialog alert = alt.create();
                alert.show();

                return false;
            }
        });

        drawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close)
        {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };




        /////////////네비게에션 드로워 검색

        ImageView drawerSearch=(ImageView)findViewById(R.id.drawer_search_btn);
        drawerSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText drawerSearchEdit = (EditText)findViewById(R.id.drawer_search_edit);
                String keyword = drawerSearchEdit.getText().toString();
                searchDrawerItem(keyword);
            }
        });

        //mDrawerLayout.addDrawerListener(drawerToggle);
        //////////////////////////여기까지 네비게이션 드로워 부분//////////////////////////
        ////////////////////////////////////////////
        //



        /////////////////플로팅 버튼////////

        fab = (FloatingActionButton)findViewById(R.id.floatingBtn);
        fab1 = (FloatingActionButton)findViewById(R.id.openDrawerfabBtn);
        fab2 = (FloatingActionButton)findViewById(R.id.addPinfabBtn);


        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);

        View.OnClickListener on = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();

                switch (id)
                {
                    case R.id.floatingBtn:
                        animateFAB();
                        break;
                    case R.id.openDrawerfabBtn:
                        if(isDrawerOpen==0) {

                            mDrawerLayout.openDrawer(GravityCompat.START);
                            isDrawerOpen=1;
                        }
                        else {
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                            isDrawerOpen=0;
                        }
                        break;
                    case R.id.addPinfabBtn:
                        getLoaction();
                        break;

                }
            }
        };


        fab.setOnClickListener(on);
        fab1.setOnClickListener(on);
        fab2.setOnClickListener(on);
        //////////////////플로팅 버튼//////////


    }






    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;

        }
    }

    //
    //네비게이션 드로어에서 아이템을 검색하는 부분.
    public void searchDrawerItem(String keyword)
    {
        ArrayList<SQLManager.Stamp> temp = new ArrayList<>();

        for(int i=0;i<results.size();i++)
        {
            if(results.get(i).title.contains(keyword))
            {
                temp.add(results.get(i));
            }
        }

        dAdapter.removeAllItem();
        for(int i=0;i<temp.size();i++)
        {
            dAdapter.addItem(temp.get(i));
        }
        dAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(mapView==null) {
            mapView = new MapView(this);
            mapView.setDaumMapApiKey("b7daa908641a4f8492a08d48ffdd441a");
            mapView.setHDMapTileEnabled(true);
            mapView.setPOIItemEventListener(this);
            mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());
            ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
            mapViewContainer.addView(mapView);
            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.5240867,126.9803881),7,true);


            //Db에서 목록을 가져온다,
            results= sqlm.SelectAll();

            //Db에서 목록을 가져와 drawer와 맵상에 데이터를 뿌려준다.
            for(int i=0;i<results.size();i++)
            {
                addPin(Double.parseDouble(results.get(i).mapx),Double.parseDouble(results.get(i).mapy),results.get(i).title,i);
                dAdapter.addItem(results.get(i));
                dAdapter.notifyDataSetChanged();
            }
        }
        else
        {
            //Db에서 목록을 가져온다,
            results= sqlm.SelectAll();
            dAdapter.removeAllItem();
            //Db에서 목록을 가져와 drawer와 맵상에 데이터를 뿌려준다.
            for(int i=0;i<results.size();i++)
            {
                addPin(Double.parseDouble(results.get(i).mapx),Double.parseDouble(results.get(i).mapy),results.get(i).title,i);
                dAdapter.addItem(results.get(i));
                dAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }


    ////
    ////
    ////
    //안드로이드의 location 퍼미션을 허용하고 결과 값을 받아오는 함수.
    /// 1은
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 2:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the

                    // contacts-related task you need to do.

                    gps = new GPSTracker(mContext, this);

                    // Check if GPS enabled
                    if (gps.canGetLocation()) {

                        while(latitude==0&&longitude==0) {
                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                        }

                        if(mapView==null) {
                            mapView = new MapView(this);
                            mapView.setDaumMapApiKey("b7daa908641a4f8492a08d48ffdd441a");
                            mapView.setHDMapTileEnabled(true);
                            mapView.setPOIItemEventListener(this);
                            mapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());

                            ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
                            mapViewContainer.addView(mapView);
                            mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.5240867,126.9803881),7,true);
                        }

                        addPin(latitude,longitude);

                    } else {
                        // Can't get location.
                        // GPS or network is not enabled.
                        // Ask user to enable GPS/network in settings.
                        gps.showSettingsAlert();
                    }

                } else {

                    Toast.makeText(mContext, "You need to grant permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    public void addPin(double latitude,double longitude)
    {
        if(tempPoitem!=null) {
            mapView.removePOIItem(tempPoitem);
        }
        MapPOIItem item = new MapPOIItem();
        item.setItemName("currentLocation");
        item.setCustomImageResourceId(R.drawable.icon_marker);
        item.setCustomImageAutoscale(true);
        item.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        item.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude,longitude));
        tempPoitem=item;
        item.setTag(-1);

        mapView.addPOIItem(item);
    }
    public void addPin(double latitude,double longitude,String title,int tagNumber)
    {
        MapPOIItem item = new MapPOIItem();
        item.setItemName(title);
        item.setCustomImageResourceId(R.drawable.icon_marker);
        item.setCustomImageAutoscale(true);
        item.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        item.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude,longitude));
        item.setTag(tagNumber);

        mapView.addPOIItem(item);
    }


    //
    //맵뷰 초기화, 현재 위치 받아온다..
    public void getLoaction()
    {
        mContext = this;

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);

        } else {
            gps = new GPSTracker(mContext, this);

            // Check if GPS enabled
            if (gps.canGetLocation()) {

                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                addPin(latitude, longitude); // 현재 위치에 핀을 추가한다.

               mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude,longitude),1,true);


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("장소 추가")
                        .setMessage("이 장소에 대한 일기를 작성하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //여기서 글 작성 목록을 띄운다.
                                Intent intent = new Intent(mapActivity.this,writeContentActivity.class);
                                intent.putExtra("longitude",longitude);
                                intent.putExtra("latitude",latitude);
                                intent.putExtra("request",1);
                                if(tempPoitem!=null) {
                                    mapView.removePOIItem(tempPoitem);
                                }
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            } else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                gps.showSettingsAlert();
            }
        }
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    /////
    //말풍선 클릭 리스너
    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        int position = mapPOIItem.getTag();
        Intent intent = new Intent(mapActivity.this,showContentActivity.class);
        intent.putExtra("request",1);
        intent.putExtra("rating",results.get(position).rating);
        intent.putExtra("title",results.get(position).title);
        intent.putExtra("mapx",results.get(position).mapx);
        intent.putExtra("mapy",results.get(position).mapy);
        intent.putExtra("tag",position);
        intent.putExtra("content",results.get(position).content);
        intent.putExtra("image_path",results.get(position).image_path);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==0)
        {
            if(resultCode==1)
            {
                int tag = data.getIntExtra("tag",-1);

                sqlm.Delete(results.get(tag).title);
                results.remove(tag);
                dAdapter.removeItem(tag);
                dAdapter.notifyDataSetChanged();
                MapPOIItem poiItems[] = mapView.getPOIItems();
                mapView.removePOIItem(poiItems[tag]);

            }
        }
    }

    public String cutString(String str)
    {
        if(str.length()>=20)
        {
            return str.substring(0,19)+"...";
        }
        else
            return str;
    }



    //여기에서 자료를 수정.
    //파일 업로드를 하기위한 객체.
    class mAsyncTask extends AsyncTask
    {
        String title;
        String content;
        String mapx;
        String mapy;
        String absolutePath;
        String rating;

        public mAsyncTask() {
            super();
        }
        public mAsyncTask(String title,String content, String mapx,String mapy,String absoulutePath,String rating) {
            this.title=title;
            this.content=content;
            this.mapx=mapx;
            this.mapy=mapy;
            this.absolutePath=absoulutePath;
            this.rating=rating;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                SharedPreferences pref = getSharedPreferences("PlayInSeoul", 0);
                String writer = pref.getString("login_userid","not Logined");

                String serverURL="http://52.78.92.129:8080/PlayInSeoul/seoul/imageUpload.jsp";

                File temp = saveBitmaptoJpeg(imageRotater.SafeDecodeBitmapFile(absolutePath));

                HttpFileUpload uploadUnit = new HttpFileUpload(serverURL,temp.getAbsolutePath());
                uploadUnit.initiate();
                uploadUnit.writeProperty("writer",writer);
                uploadUnit.writeProperty("title",title);
                uploadUnit.writeProperty("content",content);
                uploadUnit.writeProperty("mapx",mapx);
                uploadUnit.writeProperty("mapy",mapy);
                uploadUnit.writeProperty("rating",rating);
                uploadUnit.writeFile();
                uploadUnit.endTask();

                temp.deleteOnExit();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static File saveBitmaptoJpeg(Bitmap bitmap){

        OutputStream out = null;
        File fileCacheItem=null;
        try {
            fileCacheItem = File.createTempFile("temp",".jpg");
            out=new FileOutputStream(fileCacheItem);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileCacheItem;
    }

}