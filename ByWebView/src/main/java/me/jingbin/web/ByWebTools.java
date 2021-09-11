package me.jingbin.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;

/**
 * @author jingbin
 */
public class ByWebTools {

    /**
     * 将 Android5.0以下手机不能直接打开mp4后缀的链接
     *
     * @param url 视频链接
     */
    static String getVideoHtmlBody(String url) {
        return "<html>" +
                "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width\">" +
                "<style type=\"text/css\" abt=\"234\"></style>" +
                "</head>" +
                "<body>" +
                "<video controls=\"\" autoplay=\"\" name=\"media\" style=\"display:block;width:100%;position:absolute;left:0;top:20%;\">" +
                "<source src=\"" + url + "\" type=\"video/mp4\">" +
                "</video>" +
                "</body>" +
                "</html>";
    }

    /**
     * 通过包名找应用,不需要权限
     */
    public static boolean hasPackage(Context context, String packageName) {
        if (null == context || TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_GIDS);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            // 抛出找不到的异常，说明该程序已经被卸载
            return false;
        }
    }

    /**
     * 默认处理流程：网页里可能唤起其他的app
     */
    public static boolean handleThirdApp(Activity activity, String backUrl) {
        /**http开头直接跳过*/
        if (backUrl.startsWith("http")) {
            // 可能有提示下载Apk文件
            if (backUrl.contains(".apk")) {
                startActivity(activity, backUrl);
                return true;
            }
            return false;
        }
        if (backUrl.contains("alipays")) {
            // 网页跳支付宝支付
            if (hasPackage(activity, "com.eg.android.AlipayGphone")) {
                startActivity(activity, backUrl);
            }

        } else if (backUrl.contains("weixin://wap/pay")) {
            // 微信支付
            if (hasPackage(activity, "com.tencent.mm")) {
                startActivity(activity, backUrl);
            }
        } else {

            // 会唤起手机里有的App，如果不想被唤起，复制出来然后添加屏蔽即可
            boolean isJump = true;
            if (backUrl.contains("tbopen:")// 淘宝
                    || backUrl.contains("openapp.jdmobile:")// 京东
                    || backUrl.contains("jdmobile:")//京东
                    || backUrl.contains("zhihu:")// 知乎
                    || backUrl.contains("vipshop:")//
                    || backUrl.contains("youku:")//优酷
                    || backUrl.contains("uclink:")// UC
                    || backUrl.contains("ucbrowser:")// UC
                    || backUrl.contains("newsapp:")//
                    || backUrl.contains("sinaweibo:")// 新浪微博
                    || backUrl.contains("suning:")//
                    || backUrl.contains("pinduoduo:")// 拼多多
                    || backUrl.contains("qtt:")//
                    || backUrl.contains("baiduboxapp:")// 百度
                    || backUrl.contains("baiduhaokan:")// 百度看看
            ) {
                isJump = false;
            }
            if (isJump) {
                startActivity(activity, backUrl);
            }
        }
        return true;
    }

    private static void startActivity(Context context, String url) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断网络是否连通
     */
    static boolean isNetworkConnected(Context context) {
        try {
            if (context != null) {
                @SuppressWarnings("static-access")
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                return info != null && info.isConnected();
            } else {
                /**如果context为空，就返回false，表示网络未连接*/
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String getUrl(String url) {
        String urlResult = "";
        if (TextUtils.isEmpty(url)) {
            // 空url
            return urlResult;

        } else if (url.startsWith("http")) {
            // 直接返回
            return url;

        } else if (url.contains("http")) {
            // 有http且不在头部
            urlResult = url.substring(url.indexOf("http"));

        } else if (url.startsWith("www")) {
            // 以"www"开头
            urlResult = "http://" + url;

        } else if ((url.contains(".me") || url.contains(".com") || url.contains(".cn"))) {
            // 不以"http"开头且有后缀
            urlResult = "http://www." + url;

        } else if (!url.contains("www")) {
            // 输入纯文字的情况
            urlResult = "http://m5.baidu.com/s?from=124n&word=" + url;
        }
        return urlResult;
    }
}
