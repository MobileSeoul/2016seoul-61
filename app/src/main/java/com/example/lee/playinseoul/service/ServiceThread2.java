package com.example.lee.playinseoul.service;

import android.os.Handler;

public class ServiceThread2 extends Thread{
    Handler handler;
    boolean isRun = true;

    public ServiceThread2(Handler handler){
        this.handler = handler;
    }


    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
        }
    }

    public void run(){
        while(isRun){
            handler.sendEmptyMessage(0);
            try{
            }catch (Exception e) {}
            stopForever();
        }
    }
}