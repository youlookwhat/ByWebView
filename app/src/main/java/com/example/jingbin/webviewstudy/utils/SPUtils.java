package com.example.jingbin.webviewstudy.utils;

import android.content.SharedPreferences;


/**
 * Created by jingbin on 2015/2/26.
 * 用于用户没有登录时，将状态值保存到本地
 */
public class SPUtils {

    public static final String CONFIG = "config";

    public static final String CONFIG_AUTO_UPDATE = "CONFIG_AUTO_UPDATE";


    /**
     * 获取SharedPreferences实例对象
     * @param fileName 目前有 "kawsUserInfo" 、"config"
     */
    private static SharedPreferences getSharedPreference(String fileName) {
//        return ViewApplication.getInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return null;
    }

    /**
     * 保存一个String类型的值！
     */
    public static void putString(String fileName,String key, String value) {
        SharedPreferences.Editor editor = getSharedPreference(fileName).edit();
        editor.putString(key, value).apply();
    }

    /**
     * 获取String的value
     */
    public static String getString(String fileName,String key, String defValue) {
        SharedPreferences sharedPreference = getSharedPreference(fileName);
        return sharedPreference.getString(key, defValue);
    }

    /**
     * 保存一个Boolean类型的值！
     */
    public static void putBoolean(String fileName,String key, Boolean value) {
        SharedPreferences.Editor editor = getSharedPreference(fileName).edit();
        editor.putBoolean(key, value).apply();
    }
    /**
     * 获取boolean的value
     */
    public static boolean getBoolean(String fileName,String key, Boolean defValue) {
        SharedPreferences sharedPreference = getSharedPreference(fileName);
        return sharedPreference.getBoolean(key, defValue);
    }
    /**
     * 保存一个int类型的值！
     */
    public static void putInt(String fileName,String key, int value) {
        SharedPreferences.Editor editor = getSharedPreference(fileName).edit();
        editor.putInt(key, value).apply();
    }

    /**
     * 获取int的value
     */
    public static int getInt(String fileName,String key, int defValue) {
        SharedPreferences sharedPreference = getSharedPreference(fileName);
        return sharedPreference.getInt(key, defValue);
    }
    /**
     * 保存一个float类型的值！
     */
    public static void putFloat(String fileName,String key, float value) {
        SharedPreferences.Editor editor = getSharedPreference(fileName).edit();
        editor.putFloat(key, value).apply();
    }

    /**
     * 获取float的value
     */
    public static float getFloat(String fileName, String key, Float defValue) {
        SharedPreferences sharedPreference = getSharedPreference(fileName);
        return sharedPreference.getFloat(key, defValue);
    }
    /**
     * 保存一个long类型的值！
     */
    public static void putLong(String fileName,String key, long value) {
        SharedPreferences.Editor editor = getSharedPreference(fileName).edit();
        editor.putLong(key, value).apply();
    }

    /**
     * 获取long的value
     */
    public static long getLong(String fileName,String key, long defValue) {
        SharedPreferences sharedPreference = getSharedPreference(fileName);
        return sharedPreference.getLong(key, defValue);
    }

    /**
     *
     */
    public static void remove(String fileName,String key){
        SharedPreferences.Editor editor = getSharedPreference(fileName).edit();
        editor.remove(key).apply();
    }

}
