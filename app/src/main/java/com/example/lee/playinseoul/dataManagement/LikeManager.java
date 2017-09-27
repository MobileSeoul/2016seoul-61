package com.example.lee.playinseoul.dataManagement;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Hong Tae Joon on 2016-10-03.
 */

public class LikeManager {
    Handler handler;
    String userid;
    String contentid;
    int likes;
    boolean liked;

    public LikeManager(String userid, String contentid) {
        this.userid = userid;
        this.contentid = contentid;
        handler = new Handler();
    }

    public void Load(final OnLoadedListener onLoadedListener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    // 아래 코드는 서버와 연동하는 코드입니다.
                    // 서버의 특정 페이지로 아이디와 비밀번호를 POST 방식으로 넘겨준 다음
                    // 결과 값을 입력 받아서 로그인 성공 여부를 결정합니다.
                    URL url = new URL("http://52.78.92.129:8080/PlayInSeoul/getLikes.jsp");
                    HttpURLConnection huc = (HttpURLConnection)url.openConnection();
                    huc.setRequestMethod("POST");
                    huc.setDoInput(true);
                    huc.setDoOutput(true);
                    huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    OutputStream os = huc.getOutputStream();
                    String body = "userid="+userid+"&contentid="+contentid;
                    os.write(body.getBytes("euc-kr"));
                    os.flush();
                    os.close();

                    BufferedReader br = new BufferedReader( new InputStreamReader( huc.getInputStream(), "EUC-KR" ), huc.getContentLength() );
                    String buf = br.readLine();
                    buf = br.readLine();
                    String[] arr = buf.split(" ");
                    likes = Integer.parseInt(arr[0]);
                    liked = arr[1].equals("1");
                    onLoadedListener.onLoaded(likes, liked);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void like(final OnLikeListener onLikeListener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    // 아래 코드는 서버와 연동하는 코드입니다.
                    // 서버의 특정 페이지로 아이디와 비밀번호를 POST 방식으로 넘겨준 다음
                    // 결과 값을 입력 받아서 로그인 성공 여부를 결정합니다.
                    URL url = new URL("http://52.78.92.129:8080/PlayInSeoul/like.jsp");
                    HttpURLConnection huc = (HttpURLConnection)url.openConnection();
                    huc.setRequestMethod("POST");
                    huc.setDoInput(true);
                    huc.setDoOutput(true);
                    huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    OutputStream os = huc.getOutputStream();
                    String body = "userid="+userid+"&contentid="+contentid;
                    os.write(body.getBytes("euc-kr"));
                    os.flush();
                    os.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream()));
                    br.close();

                    if(onLikeListener!=null)  handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onLikeListener.onLike();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void unlike(final OnUnlikeListener onUnlikeListener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    // 아래 코드는 서버와 연동하는 코드입니다.
                    // 서버의 특정 페이지로 아이디와 비밀번호를 POST 방식으로 넘겨준 다음
                    // 결과 값을 입력 받아서 로그인 성공 여부를 결정합니다.
                    URL url = new URL("http://52.78.92.129:8080/PlayInSeoul/unlike.jsp");
                    HttpURLConnection huc = (HttpURLConnection)url.openConnection();
                    huc.setRequestMethod("POST");
                    huc.setDoInput(true);
                    huc.setDoOutput(true);
                    huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    OutputStream os = huc.getOutputStream();
                    String body = "userid="+userid+"&contentid="+contentid;
                    os.write(body.getBytes("euc-kr"));
                    os.flush();
                    os.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream()));
                    br.close();

                    if(onUnlikeListener!=null)  handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onUnlikeListener.onUnlike();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public boolean getLiked() {  return liked;  }
    public int getLikes() { return likes; }

    public interface OnLoadedListener {
        void onLoaded(int likes, boolean liked);
    }

    public interface OnLikeListener {
        void onLike();
    }

    public interface OnUnlikeListener {
        void onUnlike();
    }
}
