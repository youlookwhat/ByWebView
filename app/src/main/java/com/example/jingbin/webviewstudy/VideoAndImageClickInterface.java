package com.example.jingbin.webviewstudy;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by CZH on 2015/9/14.
 */
// js通信接口
class VideoAndImageClickInterface {
    private String tag = "VideoAndImageClickInterface";
    private Context context;

    VideoAndImageClickInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void imageClick(String img, String hasLink) {
        Toast.makeText(AppApplication.getInstance(), "----点击了图片", Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void videoClick(String img) {
//        ArrayList<String> imageuri = new ArrayList<String>();
//        imageuri.add(img);
//        Bundle bundle = new Bundle();
//        bundle.putInt("selet", 1);
//        bundle.putInt("code", 0);
//        bundle.putStringArrayList("imageuri", imageuri);
//        openActivity(ViewBigImageActivity.class, bundle);
    }

    @JavascriptInterface
    public void textClick(String kaws_type, String kaws_item_pk) {
        Toast.makeText(AppApplication.getInstance(), "----点击了文字", Toast.LENGTH_SHORT).show();
    }

}
