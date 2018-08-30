package com.example.jingbin.webviewstudy;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jingbin.webviewstudy.utils.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Link to: https://github.com/youlookwhat/WebViewStudy
 * contact me: http://www.jianshu.com/users/e43c6e979831/latest_articles
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.bt_baidu)
    TextView btBaidu;
    @BindView(R.id.bt_call)
    TextView btCall;
    @BindView(R.id.bt_upload_photo)
    TextView btUploadPhoto;
    @BindView(R.id.bt_movie)
    TextView btMovie;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;
    @BindView(R.id.bt_deeplink)
    TextView btDeepLink;
    @BindView(R.id.et_search)
    AppCompatEditText etSearch;
    @BindView(R.id.bt_openUrl)
    AppCompatButton btOpenUrl;
    @BindView(R.id.bt_java_js)
    TextView btJavaJs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorPrimary), 0);
        btBaidu.setOnClickListener(this);
        btCall.setOnClickListener(this);
        btUploadPhoto.setOnClickListener(this);
        btMovie.setOnClickListener(this);
        btJavaJs.setOnClickListener(this);
        btDeepLink.setOnClickListener(this);
        btOpenUrl.setOnClickListener(this);
        /** 处理键盘搜索键 */
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    openUrl();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_openUrl:
                openUrl();
                break;
            case R.id.bt_baidu:// 百度一下
                String baiDuUrl = "http://www.baidu.com";
                WebViewActivity.loadUrl(this, baiDuUrl, "百度一下");
                break;
            case R.id.bt_movie:// 网络视频
                String movieUrl = "https://sv.baidu.com/videoui/page/videoland?context=%7B%22nid%22%3A%22sv_5861863042579737844%22%7D&pd=feedtab_h5";
                WebViewActivity.loadUrl(this, movieUrl, "网络视频");
                break;
            case R.id.bt_upload_photo:// 上传图片
                String uploadUrl = "file:///android_asset/upload_photo.html";
                WebViewActivity.loadUrl(this, uploadUrl, "上传图片测试");
                break;
            case R.id.bt_call:// 打电话、发短信、发邮件、JS
                String callUrl = "file:///android_asset/callsms.html";
                WebViewActivity.loadUrl(this, callUrl, "电话短信邮件测试");
                break;
            case R.id.bt_java_js://  js与android原生代码互调
                String javaJs = "file:///android_asset/java_js.html";
                WebViewActivity.loadUrl(this, javaJs, "js与android原生代码互调");
                break;
            case R.id.bt_deeplink:// DeepLink通过网页跳入App
                String deepLinkUrl = "file:///android_asset/deeplink.html";
                WebViewActivity.loadUrl(this, deepLinkUrl, "DeepLink测试");
                break;
            default:
                break;
        }
    }

    /**
     * 打开网页
     */
    private void openUrl() {
        String url = etSearch.getText().toString().trim();
        if (TextUtils.isEmpty(url)) {
            url = "https://github.com/youlookwhat/WebViewStudy";
        } else if (!url.startsWith("http") && url.contains("http")) {
            url = url.substring(url.indexOf("http"), url.length());
        } else if (url.startsWith("www")) {
            url = "http://" + url;
        } else if (url.contains(".me") || url.contains(".com") || url.contains(".cn")) {
            url = "http://www." + url;
        } else if (!url.startsWith("http") && !url.contains("www")) {
            Toast.makeText(this, "您输入的链接有误", Toast.LENGTH_LONG).show();
            return;
        }
        WebViewActivity.loadUrl(this, url);
    }
}
