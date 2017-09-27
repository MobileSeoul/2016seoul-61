package com.example.lee.playinseoul;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Hong on 2016-08-30.
 */
public class SQLManager extends SQLiteOpenHelper {
    public SQLManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE stamp ( title TEXT, mapx TEXT, mapy TEXT, content TEXT, image_path TEXT, rating TEXT );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public void Insert(String title, double mapx, double mapy, String content, String image_path,String rating) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO stamp VALUES ( '" + title + "', " + mapx + ", " + mapy + ", '" + content + "', '" + image_path+ "', '" +rating+ "' );");
    }

    public void Insert(String title, String mapx, String mapy, String content, String image_path,float rating) {
        Insert(title, Double.parseDouble(mapx), Double.parseDouble(mapy), content, image_path,rating+"");
    }

    public void Update(String oldtitle,String title,String content,String image_path,String rating)
    {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title",title);
        values.put("content",content);
        values.put("rating",rating);
        values.put("image_path",image_path);
        db.update("stamp",values,"title=?",new String[]{oldtitle});
    }


    public void Insert(Stamp stamp) {
        Insert(stamp.title, stamp.mapx, stamp.mapy, stamp.content, stamp.image_path,Float.parseFloat(stamp.rating));
    }

    public void Delete(String title) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM stamp WHERE title='" + title + "';");
    }

    public void Delete(Stamp stamp) {
        Delete(stamp.title);
    }

    public ArrayList<Stamp> SelectAll() {
        ArrayList<Stamp> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM stamp", null);
        while(cursor.moveToNext()) {
            String title = cursor.getString(0);
            String mapx = cursor.getString(1);
            String mapy = cursor.getString(2);
            String content = cursor.getString(3);
            String image_path = cursor.getString(4);
            String rating = cursor.getString(5);
            Stamp stamp = new Stamp(title, mapx, mapy, content, image_path,rating);
            list.add(stamp);
        }

        return list;
    }

    public class Stamp {
        public String title, content, image_path;
        public String mapx, mapy;
        public String rating;

        public Stamp(String title, String mapx, String mapy, String content, String image_path,String rating) {
            this.title = title;
            this.content = content;
            this.image_path = image_path;
            this.mapx = mapx;
            this.mapy = mapy;
            this.rating=rating;
        }
    }
}
