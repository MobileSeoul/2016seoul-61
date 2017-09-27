

package com.example.lee.playinseoul.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lee.playinseoul.BigImageActivity;
import com.example.lee.playinseoul.DetailActivity;
import com.example.lee.playinseoul.R;
import com.example.lee.playinseoul.model.Tour;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

/**
 * Created by LEE on 2016-08-09.
 */
public class TourAdapter extends ArrayAdapter<Tour> {
    Activity activity;
    int resource;

    ImageLoader imageLoader;
    DisplayImageOptions options;

    Context context;

    public static ProgressDialog mprogressDialog;

    public TourAdapter(Context context, int resource, List<Tour> objects) {
        super(context, resource, objects);

        this.context=context;

        activity = (Activity) context;
        this.resource = resource;

        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.showImageOnLoading(R.drawable.image_none);
        builder.showImageForEmptyUri(R.drawable.image_none);
        builder.showImageOnFail(R.drawable.image_none);
        builder.cacheInMemory(true);
        builder.cacheOnDisc(true);
        builder.considerExifParams(true);
        options =builder.build();

        imageLoader = ImageLoader.getInstance();
        if(!imageLoader.isInited()){
            ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(activity);
            imageLoader.init(config);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View itemView = convertView;



        if(itemView == null) {
            itemView = activity.getLayoutInflater().inflate(this.resource, null);
            //convertView.setTag(getItem(position)); // 그때의 Tour객체를 set해준다.
        }

        Tour tour = getItem(position);

        if(tour != null){
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            final TextView textView1 = (TextView) itemView.findViewById(R.id.textView1);
            TextView textView2 = (TextView) itemView.findViewById(R.id.textView2);

            imageLoader.displayImage(tour.getFirstimage(), imageView, options);
            // 색상 필터 입히는 부분
            imageView.setColorFilter(Color.parseColor("#44000000"));

            textView1.setText(tour.getTitle());
            textView2.setText(tour.getAddr1());


            //각각의 버튼 이벤트 클릭 리스너 셋해줌
            ImageView btn = (ImageView) itemView.findViewById(R.id.specificBtn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Tour tours = getItem(position);
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("contentid",tours.getContentId());
                    intent.putExtra("imageurl",tours.getFirstimage());
                    intent.putExtra("mapx",tours.getMapx());
                    intent.putExtra("mapy",tours.getMapy());
                    intent.putExtra("title",tours.getTitle());
                    intent.putExtra("contentType",tours.getContenttypeid());

                    context.startActivity(intent);

                    //여기서 image url이랑 contentid를 넘겨줘서 자세히보기 액티비티 시작.
                }
            });

            //각각의 이미지 클릭 리스너 셋해줌.
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Tour tours = getItem(position);

                    Intent intent = new Intent(context, BigImageActivity.class);
                    intent.putExtra("contentid",tours.getContentId());
                    intent.putExtra("contentType",tours.getContenttypeid());
                    intent.putExtra("request",1);//
                    mprogressDialog = ProgressDialog.show(context, "", "이미지를 불러오는 중입니다.");

                    context.startActivity(intent);

                }
            });
        }
        return itemView;
    }

}