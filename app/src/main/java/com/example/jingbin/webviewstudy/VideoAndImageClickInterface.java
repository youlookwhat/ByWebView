package com.example.jingbin.webviewstudy;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Created by CZH on 2015/9/14.
 */
// js通信接口
public class VideoAndImageClickInterface {
    private String tag = "VideoAndImageClickInterface";
    private Context context;

    public VideoAndImageClickInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void imageClick(String img, String hasLink) {
//        DebugUtil.debug("hasLink :" + hasLink);
//        if (!TextUtils.isEmpty(hasLink) && hasLink.equalsIgnoreCase("1")) {
//            return;
//        }
//        ArrayList<String> imageuri = new ArrayList<String>();
//        imageuri.add(img);
//        Bundle bundle = new Bundle();
//        bundle.putInt("selet", 1);
//        bundle.putInt("code", 0);
//        bundle.putStringArrayList("imageuri", imageuri);
//        Intent intent = new Intent(context, ViewBigImageActivity.class);
//        intent.putExtras(bundle);
//        context.startActivity(intent);
        Log.e("imageClick ","---点击了图片");
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
    public void textClick(Context context,String kaws_type, String kaws_item_pk) {
        Log.e("textClick ","---点击了文字");
        return;
//        WebViewActivity.loadUrl(context,"https://www.baidu.com");
//        Log.e("text",kaws_type+":"+kaws_item_pk);
//        JumpItemBean jumpItemBean = new JumpItemBean();
//        jumpItemBean.setType_id(Integer.parseInt(kaws_type));
//        jumpItemBean.setItem_pk(kaws_item_pk);
//        jumpItemBean.setId(kaws_item_pk);
//        ApplicationConfig.FocusJumpApp(context, jumpItemBean, ApplicationConfig.JUMP_FROM_INFORMATION_DETAIL_IMAGE, Integer.parseInt(kaws_type));

    }

}
