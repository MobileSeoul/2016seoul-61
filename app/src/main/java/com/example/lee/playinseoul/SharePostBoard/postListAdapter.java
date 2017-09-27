package com.example.lee.playinseoul.SharePostBoard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.lee.playinseoul.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by xyom on 2016-08-29.
 */
public class postListAdapter extends BaseAdapter {
    private Context mContext= null;
    private ArrayList<postMainActivity.postData> mListData = new ArrayList<>();

    ImageLoader imageLoader= null;
    DisplayImageOptions options;

    public postListAdapter(Context mContext)
    {
        super();
        this.mContext=mContext;

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
            ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(mContext);
            imageLoader.init(config);
        }

    }
    public int getCount()
    {
        return mListData.size();
    }

    public postMainActivity.postData getItem(int position)
    {
        return mListData.get(position);
    }
    public long getItemId(int position)
    {
        return position;
    }
    public void addItem(postMainActivity.postData postData)
    {
        mListData.add(postData);
    }
    public void removeItem(int position){mListData.remove(position);}
    public void removeAllItem(){mListData.clear();}
    public View getView(int position, View converView, ViewGroup parent)
    {
        ViewHolder holder;

        if(converView==null)
        {
            holder =new ViewHolder();

            LayoutInflater inflater= (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            converView=inflater.inflate(R.layout.post_item,null);

            holder.postBackground=(ImageView)converView.findViewById(R.id.postBackground);
            holder.postBackground.setColorFilter((Color.parseColor("#44000000")));
            holder.writer=(TextView)converView.findViewById(R.id.postWriter);
            holder.date=(TextView)converView.findViewById(R.id.postDate);
            holder.title=(TextView)converView.findViewById(R.id.postTitle);
            holder.rating=(RatingBar) converView.findViewById(R.id.postRating);
            converView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)converView.getTag();
        }

        String writer = mListData.get(position).postWriter;
        String _wr = writer.replaceAll("\r\n", "");
        String date = mListData.get(position).date;
        String title = mListData.get(position).title;
        Float rating = Float.parseFloat(mListData.get(position).rating);

        imageLoader.displayImage("http://52.78.92.129:8080/PlayInSeoul/seoul/images/"+mListData.get(position).image_path,holder.postBackground,options);
        holder.writer.setText(_wr + " 님의 추천");
        holder.date.setText(date);
        holder.title.setText(title);
        holder.rating.setRating(rating);
        holder.rating.setIsIndicator(true);

        return converView;
    }

    class ViewHolder
    {
        ImageView postBackground;
        TextView writer;
        TextView date;
        TextView title;
        RatingBar rating;
        TextView content;
    }

}




