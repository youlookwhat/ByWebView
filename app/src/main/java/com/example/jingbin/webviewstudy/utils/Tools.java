package com.example.jingbin.webviewstudy.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * @author jingbin
 * @data 2018/1/19
 * @Description
 */

public class Tools {

    /**
     * 判断手机是否安装某个应用
     *
     * @param context
     * @param appPackageName 应用包名
     * @return true：安装，false：未安装
     */
    public static boolean isApplicationAvilible(Context context, String appPackageName) {
        try {
            // 获取packagemanager
            PackageManager packageManager = context.getPackageManager();
            // 获取所有已安装程序的包信息
            List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
            if (pinfo != null) {
                for (int i = 0; i < pinfo.size(); i++) {
                    String pn = pinfo.get(i).packageName;
                    if (appPackageName.equals(pn)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception ignored) {
            return false;
        }
    }
}
