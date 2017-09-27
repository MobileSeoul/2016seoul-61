package com.example.lee.playinseoul.StampMap;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lee.playinseoul.R;
import com.example.lee.playinseoul.SQLManager;
import com.example.lee.playinseoul.model.StampIconView;

import java.util.ArrayList;

/**
 * Created by xyom on 2016-08-29.
 */
public class DrawerListAdapter extends BaseAdapter {
    private Context mContext= null;
    private ArrayList<SQLManager.Stamp> mListData = new ArrayList<>();

    public DrawerListAdapter(Context mContext)
    {
        super();
        this.mContext=mContext;
    }
    public int getCount()
    {
        return mListData.size();
    }

    public SQLManager.Stamp getItem(int position)
    {
        return mListData.get(position);
    }
    public long getItemId(int position)
    {
        return position;
    }
    public void addItem(SQLManager.Stamp stamp)
    {
        mListData.add(stamp);
    }
    public void removeItem(int position){mListData.remove(position);}
    public void removeAllItem(){mListData.clear();}

    // todo 평점대 별로 아이콘 색상 변경
//    평점 5점 #7667B2
//    평점 4점대 #9178C1
//    평점 3점대 #AE90DD
//    평점 2점대 #BDAAE0
//    평점 1점대 #D3CAE8
    public View getView(int position, View converView, ViewGroup parent)
    {

        ViewHolder holder;
        if(converView==null)
        {
            holder =new ViewHolder();

            LayoutInflater inflater= (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            converView=inflater.inflate(R.layout.map_drawer_item,null);

            holder.title=(TextView)converView.findViewById(R.id.drawerTitle);
            holder.rating=(TextView)converView.findViewById(R.id.ratingText);

            converView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)converView.getTag();
        }

        String title = mListData.get(position).title;
        String rating = mListData.get(position).rating;

        holder.title.setText(title);
        holder.rating.setText("평점:"+rating);

        StampIconView iconView = (StampIconView)converView.findViewById(R.id.drawer_item_image);
        int color;
        float floatRating = Float.parseFloat(rating);
        if(floatRating >= 4) color = Color.parseColor("#7667B2");
        else if (floatRating >= 3) color = Color.parseColor("#9178C1");
        else if (floatRating >= 2) color = Color.parseColor("#AE90DD");
        else if (floatRating >= 1) color = Color.parseColor("#BDAAE0");
        else  color = Color.parseColor("#D3CAE8");
        iconView.setColor(color);
        return converView;
    }

    class ViewHolder
    {
        TextView title;
        TextView rating;
    }

}



