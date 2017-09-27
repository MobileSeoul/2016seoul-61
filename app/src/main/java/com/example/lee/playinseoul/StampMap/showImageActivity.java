package com.example.lee.playinseoul.StampMap;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.lee.playinseoul.R;
import com.example.lee.playinseoul.SharePostBoard.imageRotater;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by xyom on 2016-09-26.
 */

public class showImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_show);

        ptc_SetScreenSize();

        Intent intent = getIntent();

        int request = intent.getIntExtra("request",0);
        Log.d("request",request+"");
        String image_path = intent.getStringExtra("image_path");

        ImageView imageView =(ImageView)findViewById(R.id.show_big_image);

        if(request==1)
        {
            Cursor c = getContentResolver().query(Uri.parse(image_path), null, null, null, null);
            c.moveToNext();
            try {
                final String absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));

                try {
                    imageView.setImageBitmap(imageRotater.SafeDecodeBitmapFile(absolutePath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch(Exception e)
            {
                imageView.setImageResource(R.drawable.image_none);
            }
        }
        else if(request==2)
        {
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

            imageLoader.displayImage("http://52.78.92.129:8080/PlayInSeoul/seoul/images/"+image_path,imageView,options);
        }
    }

    private void ptc_SetScreenSize() {
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        int width = (int) (display.getWidth() * 0.95); //Display 사이즈의 70%

        int height = (int) (display.getHeight() * 0.9);  //Display 사이즈의 90%

        getWindow().getAttributes().width = width;

        getWindow().getAttributes().height = height;
    }

}
