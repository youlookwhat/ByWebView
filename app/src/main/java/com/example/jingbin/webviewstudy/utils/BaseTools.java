package com.example.jingbin.webviewstudy.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.example.jingbin.webviewstudy.App;
import com.example.jingbin.webviewstudy.R;

/**
 * Created by jingbin on 2017/2/13.
 */

public class BaseTools {

    /**
     * 实现文本复制功能
     *
     * @param content 复制的文本
     */
    public static void copy(String content) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) App.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
        if (cmb != null) {
            cmb.setText(content.trim());
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
}
