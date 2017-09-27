package com.example.lee.playinseoul.StampMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Rating;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lee.playinseoul.FindWayActivity;
import com.example.lee.playinseoul.R;
import com.example.lee.playinseoul.SQLManager;
import com.example.lee.playinseoul.SearchActivity;
import com.example.lee.playinseoul.SharePostBoard.imageRotater;
import com.example.lee.playinseoul.TourActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * Created by xyom on 2016-09-13.
 */
public class showContentActivity extends AppCompatActivity {



    int showContent=1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();

        int request = intent.getIntExtra("request",-1);

        setContentView(R.layout.show_content_layout);

        final RatingBar rating = (RatingBar)findViewById(R.id.show_rating);
        final TextView title = (TextView)findViewById(R.id.show_title);
        final TextView content = (TextView)findViewById(R.id.show_content);
        final ImageView image = (ImageView)findViewById(R.id.show_image);
        final TextView BigTitle = (TextView)findViewById(R.id.show_Big_title);
        final Button location = (Button)findViewById(R.id.show_findLocationBtn);
        final ImageButton share = (ImageButton)findViewById(R.id.show_content_share);
        final ImageButton edit = (ImageButton)findViewById(R.id.show_content_edit);
        final ImageButton delete = (ImageButton)findViewById(R.id.show_content_delete);
        final TextView textRating = (TextView)findViewById(R.id.show_text_rating);
        LinearLayout layoutButtons = (LinearLayout)findViewById(R.id.show_layout_button);

        switch (request)
        {
            // 일반 화면에서 콘텐츠 보기를 들어 왔을때
            case 1:
                final String trating = intent.getStringExtra("rating");
                final String ttitle = intent.getStringExtra("title");
                final String tcontent = intent.getStringExtra("content");
                final String timage = intent.getStringExtra("image_path");
                final String tmapx = intent.getStringExtra("mapx");
                final String tmapy = intent.getStringExtra("mapy");
                final int position = intent.getIntExtra("tag",-3);

                share.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);

                // 제목 바꿔주고
                BigTitle.setText("내 일기 보기");
                // 평점 표시해주고
                textRating.setText("평점 : " + trating);
                rating.setRating(Float.parseFloat(trating));
                rating.setIsIndicator(true);
                // 이상한 문자 제거하고 후기 제목 표시
                String str = ttitle.replaceAll("\r\n", "");
                title.setText(str);
                // 버튼들을 보여주고
                layoutButtons.setVisibility(View.VISIBLE);
                // 여행 가기 버튼 없애주고
                location.setVisibility(View.GONE);

                //공유하기 버튼 클릭시
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder alt = new AlertDialog.Builder(showContentActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                        alt.setTitle("공유하시겠습니까?");
                        alt.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String title = ttitle;
                                String content = tcontent;
                                String mapx = tmapx;
                                String mapy = tmapy;
                                String image_path = timage;
                                String rating = trating;
                                try {
                                    Cursor c = getContentResolver().query(Uri.parse(image_path), null, null, null, null);
                                    c.moveToNext();
                                    final String absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));

                                    mAsyncTask task = new mAsyncTask(title, content, mapx, mapy, absolutePath, rating);
                                    task.execute();
                                    Toast.makeText(showContentActivity.this, "공유 되었습니다.", Toast.LENGTH_SHORT).show();
                                }catch(Exception e )
                                {
                                    Toast.makeText(showContentActivity.this, "공유할 사진이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                }

                                finish();
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

                    }
                });

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(showContentActivity.this,writeContentActivity.class);
                        intent.putExtra("title",ttitle);
                        intent.putExtra("content",tcontent);
                        intent.putExtra("rating",trating);
                        intent.putExtra("image_path",timage);
                        intent.putExtra("request",2);
                        startActivity(intent);
                        finish();

                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alt = new AlertDialog.Builder(showContentActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                        alt.setTitle("삭제하시겠습니까?");
                        alt.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.putExtra("tag",position);
                                showContentActivity.this.setResult(1,intent);
                                finish();
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
                    }
                });


                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(showContentActivity.this,showImageActivity.class);
                        intent.putExtra("request",1);
                        intent.putExtra("image_path",timage);
                        startActivity(intent);
                    }
                });


                content.setText(tcontent);
                Cursor c = getContentResolver().query(Uri.parse(timage), null, null, null, null);
                c.moveToNext();
                try {
                    final String absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));

                    try {
                        image.setImageBitmap(imageRotater.SafeDecodeBitmapFile(absolutePath));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }catch(Exception e)
                {
                    image.setImageResource(R.drawable.image_none);
                }

                break;

            // postShare 클릭에서 콘텐츠 보기 화면으로 들어 왔을떄
            case 2:
                final String titleValue = intent.getStringExtra("title");
                String content1 = intent.getStringExtra("content");
                String writer1 = intent.getStringExtra("writer");
                Float rating1 = Float.parseFloat(intent.getStringExtra("rating"));
                final String mapx = intent.getStringExtra("mapx");
                final String mapy = intent.getStringExtra("mapy");
                final String image_path = intent.getStringExtra("image_path");
                String date = intent.getStringExtra("date");

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(showContentActivity.this,showImageActivity.class);
                        intent.putExtra("request",2);
                        intent.putExtra("image_path",image_path);
                        startActivity(intent);
                    }
                });

                // 제목 바꿔주고
                BigTitle.setText("자세히보기");
                // 후기 제목 바꿔주고
                String replaceTitle = titleValue.replaceAll("\r\n", "");
                title.setText(replaceTitle);
                // 평점 뿌려주고
                textRating.setText("평점 : " + rating1);
                rating.setIsIndicator(true);
                rating.setRating(rating1);
                // 버튼들 숨겨주고
                layoutButtons.setVisibility(View.GONE);
                // 여행 가기 버튼은 보여주고
                location.setVisibility(View.VISIBLE);

                location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(showContentActivity.this, FindWayActivity.class);
                        intent.putExtra("title",titleValue);
                        intent.putExtra("mapx",mapy);
                        intent.putExtra("mapy",mapx);
                        startActivity(intent);
                    }
                });

                DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
                builder.showImageOnLoading(R.drawable.image_none);
                builder.showImageForEmptyUri(R.drawable.image_none);
                builder.showImageOnFail(R.drawable.image_none);
                builder.cacheInMemory(true);
                builder.cacheOnDisc(true);
                builder.considerExifParams(true);
                DisplayImageOptions options =builder.build();

                ImageLoader imageLoader = ImageLoader.getInstance();
                if(!imageLoader.isInited()){
                    ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
                    imageLoader.init(config);
                }

                imageLoader.displayImage("http://52.78.92.129:8080/PlayInSeoul/seoul/images/"+image_path,image,options);

                content1 = content1.replaceAll("\\u0000","");
                content1 = content1.replaceAll("\\uFFFD","");
                content.setText(content1);

        }


    }

    private void ptc_SetScreenSize() {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        int width = (int) (display.getWidth() * 0.95); //Display 사이즈의 70%

        int height = (int) (display.getHeight() * 0.9);  //Display 사이즈의 90%

        getWindow().getAttributes().width = width;

        getWindow().getAttributes().height = height;
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
