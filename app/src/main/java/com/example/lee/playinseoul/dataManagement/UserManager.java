package com.example.lee.playinseoul.dataManagement;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Hong Tae Joon on 2016-10-03.
 */

public class UserManager {
    public static String getLoginUserId(Context context) {
        SharedPreferences pref = context.getSharedPreferences("PlayInSeoul", 0);
        return pref.getString("login_userid", "");
    }
    public static String getLoginPassword(Context context) {
        SharedPreferences pref = context.getSharedPreferences("PlayInSeoul", 0);
        return pref.getString("login_password", "");
    }
    public static boolean getAutoLogined(Context context) {
        SharedPreferences pref = context.getSharedPreferences("PlayInSeoul", 0);
        return pref.getBoolean("auto_login", false);
    }
}
