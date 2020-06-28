package me.jingbin.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;


/**
 * Created by jingbin on 2017/2/13.
 */

public class ByWebTools {

    /**
     * 将 Android5.0以下手机不能直接打开mp4后缀的链接
     *
     * @param url 视频链接
     */
    public static String getVideoHtmlBody(String url) {
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
     * Android 6.0以下处理方法：
     * 1）判断 Html页面的标题中是否含有“Error”、“找不到网页”等信息；
     */
    public static void handleErrorHtml(WebView webView, String title) {
        // android 6.0 以下通过title获取判断
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (title.contains("404")
                    || title.contains("500")
                    || title.contains("Error")
                    || title.contains("找不到网页")
                    || title.contains("网页无法打开")) {
                String mErrorUrl = "file:///android_asset/404_error.html";
                webView.loadUrl(mErrorUrl);
            }
        }
    }

    /**
     * 2）重写WebChromeClient的onReceivedError()方法处理（该方法已过时）
     */
    public static void handleReceivedError(WebView webView) {
        //6.0以下执行
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return;
        }
        String mErrorUrl = "file:///android_asset/404_error.html";
        webView.loadUrl(mErrorUrl);
    }

    /**
     * Android 6.0以上处理方法：
     * 重写WebViewClient的onReceivedHttpError()方法，判断错误码来处理；
     */
    public static void handleReceivedHttpError(WebView webView, WebResourceResponse errorResponse) {
        // 这个方法在 android 6.0才出现
        int statusCode = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusCode = errorResponse.getStatusCode();
        }
        if (404 == statusCode || 500 == statusCode) {
            String mErrorUrl = "file:///android_asset/404_error.html";
            webView.loadUrl(mErrorUrl);
        }
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
     * 处理三方链接
     * 网页里可能唤起其他的app
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

        boolean isJump = true;
        /**屏蔽以下应用唤起App，可根据需求 添加或取消*/
        if (
                backUrl.startsWith("tbopen:")// 淘宝
//                        || backUrl.startsWith("openapp.jdmobile:")// 京东
//                        || backUrl.startsWith("jdmobile:")//京东
//                        || backUrl.startsWith("alipay:")// 支付宝
//                        || backUrl.startsWith("alipays:")//支付宝
                        || backUrl.startsWith("zhihu:")// 知乎
                        || backUrl.startsWith("vipshop:")//
                        || backUrl.startsWith("youku:")//优酷
                        || backUrl.startsWith("uclink:")// UC
                        || backUrl.startsWith("ucbrowser:")// UC
                        || backUrl.startsWith("newsapp:")//
                        || backUrl.startsWith("sinaweibo:")// 新浪微博
                        || backUrl.startsWith("suning:")//
                        || backUrl.startsWith("pinduoduo:")// 拼多多
                        || backUrl.startsWith("baiduboxapp:")// 百度
                        || backUrl.startsWith("qtt:")//
        ) {
            isJump = false;
        }
        if (isJump) {
            startActivity(activity, backUrl);
        }
        return isJump;
    }

    private static void startActivity(Context context, String url) {
        try {

            // 用于DeepLink测试
            if (url.startsWith("will://")) {
                Uri uri = Uri.parse(url);
                Log.e("---------scheme", uri.getScheme() + "；host: " + uri.getHost() + "；Id: " + uri.getPathSegments().get(0));
            }

            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
