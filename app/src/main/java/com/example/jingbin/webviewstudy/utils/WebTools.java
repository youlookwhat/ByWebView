package com.example.jingbin.webviewstudy.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.jingbin.webviewstudy.App;
import com.example.jingbin.webviewstudy.BuildConfig;
import com.example.jingbin.webviewstudy.R;

/**
 * Created by jingbin on 2017/2/13.
 */

public class WebTools {

    /**
     * 将 Android5.0以下手机不能直接打开mp4后缀的链接
     *
     * @param url 视频链接
     */
    public static String getVideoHtmlBody(String title, String url) {
        return "<html>" +
                "<head>" +
                "<meta name=\"viewport\" content=\"width=device-width\">" +
                "<title>" + title + "</title>" +
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
     * 实现文本复制功能
     *
     * @param content 复制的文本
     */
    public static void copy(String content) {
        if (!TextUtils.isEmpty(content)) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                ClipboardManager clipboard = (ClipboardManager) App.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(content);
            } else {
                ClipboardManager clipboard = (ClipboardManager) App.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(content, content);
                clipboard.setPrimaryClip(clip);
            }
        }
    }

    /**
     * 使用浏览器打开链接
     */
    public static void openLink(Context context, String content) {
        if (!TextUtils.isEmpty(content) && content.startsWith("http")) {
            Uri issuesUrl = Uri.parse(content);
            Intent intent = new Intent(Intent.ACTION_VIEW, issuesUrl);
            context.startActivity(intent);
        }
    }

    /**
     * 分享
     */
    public static void share(Context context, String extraText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.action_share));
        intent.putExtra(Intent.EXTRA_TEXT, extraText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.action_share)));
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

    private static void startActivity(Activity context, String url) {
        try {

            // 用于DeepLink测试
            if (url.startsWith("will://")) {
                Uri uri = Uri.parse(url);
                Log.e("---------scheme", uri.getScheme() + "；host: " + uri.getHost() + "；Id: " + uri.getPathSegments().get(0));
            }

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }


    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void showToast(String content) {
        if (!TextUtils.isEmpty(content)) {
            Toast.makeText(App.getInstance(), content, Toast.LENGTH_SHORT).show();
        }
    }
}
