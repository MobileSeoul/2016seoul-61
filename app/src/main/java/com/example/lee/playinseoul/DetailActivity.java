package com.example.lee.playinseoul;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lee.playinseoul.dataManagement.LikeManager;
import com.example.lee.playinseoul.dataManagement.SmallGroupManager;
import com.example.lee.playinseoul.dataManagement.UserManager;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.net.URL;

public class DetailActivity extends AppCompatActivity {
    String contentid, title, address, tema, content, image_url, mapx,mapy,contentType;
    ProgressDialog progressDialog;
    moreDataSet data;
    Bitmap main_image;
    boolean liked;
    int likes;

    public static ProgressDialog mprogressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 인텐트 값을 가져오는 부분
        Intent intent = getIntent();
        // 인텐트가 없는경우에는 무시
        if(intent==null) return;
        contentid = intent.getStringExtra("contentid");
        image_url = intent.getStringExtra("imageurl");
        mapx=intent.getStringExtra("mapx");
        mapy=intent.getStringExtra("mapy");
        title=intent.getStringExtra("title");
        contentType=intent.getStringExtra("contentType");

        // 이미지를 눌렀을 때
        ImageView imageView = (ImageView)findViewById(R.id.detail_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this,BigImageActivity.class);
                intent.putExtra("contentid",contentid);
                intent.putExtra("contentType",contentType);
                intent.putExtra("request",2);
                mprogressDialog = ProgressDialog.show(DetailActivity.this, "", "이미지를 불러오는 중입니다.");
                startActivity(intent);
            }
        });

        if(contentid!=null) {
            LoadInformation(contentid);
        }

        // 길찾기 버튼을 눌렀을 때
        findViewById(R.id.btnFind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this,FindWayActivity.class);
                intent.putExtra("mapx",mapx);
                intent.putExtra("mapy",mapy);
                intent.putExtra("title",title);
                intent.putExtra("contentid", contentid);
                intent.putExtra("image_url",image_url);
                intent.putExtra("contentType", contentType);

                startActivity(intent);
            }
        });

        // 좋아요 버튼을 설정하는 부분
        findViewById(R.id.detail_check_like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(liked) {
                    liked = false;
                    likes--;
                    LikeManager likeManager = new LikeManager(UserManager.getLoginUserId(DetailActivity.this), contentid);
                    likeManager.unlike(new LikeManager.OnUnlikeListener() {
                        @Override
                        public void onUnlike() {
                            Toast.makeText(DetailActivity.this, "'" + title + "' 장소를 좋아하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    liked = true;
                    likes++;
                    LikeManager likeManager = new LikeManager(UserManager.getLoginUserId(DetailActivity.this), contentid);
                    likeManager.like(new LikeManager.OnLikeListener() {
                        @Override
                        public void onLike() {
                            Toast.makeText(DetailActivity.this, "'" + title + "' 장소를 좋아합니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                setLikePart(likes, liked);
            }
        });
    }

    // 맵뷰를 설정하는 부분
    private void SetMapView(ViewGroup mapViewContainer, double mapx, double mapy) {
        MapView mapView = new MapView(this);
        mapView.setDaumMapApiKey("b7daa908641a4f8492a08d48ffdd441a");
        mapView.setHDMapTileEnabled(true);
        mapViewContainer.addView(mapView);
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(mapy, mapx),3,true);

        // 핀을 표시해주는 부분
        MapPOIItem item = new MapPOIItem();
        item.setItemName(title);
        item.setCustomImageResourceId(R.drawable.icon_marker);
        item.setCustomImageAutoscale(true);
        item.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        item.setMapPoint(MapPoint.mapPointWithGeoCoord(mapy,mapx));
        item.setCustomImageAnchor(0.5f, 1.0f);
        item.setTag(-1);

        mapView.addPOIItem(item);
    }

    private void LoadInformation(final String contentid) {
        progressDialog = ProgressDialog.show(DetailActivity.this, "", "정보를 불러오는 중입니다.");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getInformation g = new getInformation();
                    data = g.getSpecificData(Integer.parseInt(contentid));

                    handler.sendEmptyMessage(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    // 대표 이미지를 불러오고 설정하는 부분
    private void LoadImage() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(image_url);
                    main_image = BitmapFactory.decodeStream(url.openStream());
                } catch (Exception e) {
                    main_image = BitmapFactory.decodeResource(getResources(), R.drawable.image_none);
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        });
        thread.start();
    }

    // 데이터를 모두 불러왔을 때 부분
    // 텍스트를 출력한다
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0) {
                ImageView imageView = (ImageView)findViewById(R.id.detail_image);
                imageView.setImageBitmap(main_image);
            } else if(msg.what == 1) {
                // 상단 이미지
                LoadImage();

                // 상단 타이틀 텍스트
                TextView textTitle = (TextView)findViewById(R.id.detail_text_title);
                title = cutString(data.title, 15);
                textTitle.setText(title);

                // 이미지 밑에 타이틀과 주소
                TextView text_title = (TextView) findViewById(R.id.detail_title);
                TextView textAddress = (TextView)findViewById(R.id.detail_text_address);
                text_title.setText(title);
                address = "";
                if(data.addr1 != null) address += data.addr1;
                if(data.addr2 != null) address += " " + data.addr2;
                textAddress.setText(address);

                // 좋아요 부분
                String userid = UserManager.getLoginUserId(DetailActivity.this);
                LikeManager likeManager = new LikeManager(userid, contentid);
                likeManager.Load(new LikeManager.OnLoadedListener() {
                    @Override
                    public void onLoaded(int likes, boolean liked) {
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putInt("likes", likes);
                        bundle.putBoolean("liked", liked);
                        message.setData(bundle);
                        message.what=3;
                        handler.sendMessage(message);
                    }
                });

                // 중간 부분에 자치구와 테마
                TextView text_gu = (TextView) findViewById(R.id.detail_text_gu);
                TextView textTema = (TextView)findViewById(R.id.detail_text_tema);
                tema = SmallGroupManager.getGroupName(data.cat3);
                textTema.setText(tema);
                String[] arr_add = address.split(" ");
                String gu = arr_add[0];
                if (arr_add.length > 1) gu = arr_add[1];
                text_gu.setText(gu);

                // 하단 자세한 내용
                TextView textContent = (TextView)findViewById(R.id.detail_text_content);
                content = data.overview.replace("<br>", "\n");
                content = content.replace("<br />", "\n");
                content = content.replace("&amp;", "");
                content = content.replace("&nbsp;", " ");
                textContent.setText(content);

                // 맵뷰를 넣어주는 부분
                ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.detail_map_view);
                double _mapx = Double.parseDouble(mapx);
                double _mapy = Double.parseDouble(mapy);
                SetMapView(mapViewContainer, _mapx, _mapy);

                // 데이터를 모두 설정시키고 1초정도 후에 보여준다.
                handler.sendEmptyMessageDelayed(2, 1000);
            } else if(msg.what == 2) {
                progressDialog.dismiss();
            } else if(msg.what == 3) {
                // 좋아요 결과 값에 따라 설정해주는 부분
                int likes = msg.getData().getInt("likes", 0);
                boolean liked = msg.getData().getBoolean("liked", false);
                setLikePart(likes, liked);
            }
        }
    };

    private void setLikePart(int likes, boolean liked) {
        ImageView image_like = (ImageView)findViewById(R.id.detail_check_like);
        if(liked) image_like.setImageResource(R.drawable.icon_button_like_liked);
        else image_like.setImageResource(R.drawable.icon_button_like);
        TextView text_like= (TextView)findViewById(R.id.detail_text_like);
        text_like.setText(""+likes);
        DetailActivity.this.liked = liked;
        DetailActivity.this.likes = likes;
    }

    private String cutString(String str, int size) {
        if(str.length() < size) return str;
        return str.substring(0,size) + "...";
    }
}
