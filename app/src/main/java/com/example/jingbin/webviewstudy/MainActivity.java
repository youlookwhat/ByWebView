package com.example.jingbin.webviewstudy;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jingbin.webviewstudy.utils.StatusBarUtil;

/**
 * Link to: https://github.com/youlookwhat/WebViewStudy
 * contact me: https://www.jianshu.com/u/e43c6e979831
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatEditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorPrimary), 0);
        initView();
    }

    private void initView() {
        findViewById(R.id.bt_deeplink).setOnClickListener(this);
        findViewById(R.id.bt_openUrl).setOnClickListener(this);
        findViewById(R.id.bt_baidu).setOnClickListener(this);
        findViewById(R.id.bt_movie).setOnClickListener(this);
        findViewById(R.id.bt_upload_photo).setOnClickListener(this);
        findViewById(R.id.bt_call).setOnClickListener(this);
        findViewById(R.id.bt_java_js).setOnClickListener(this);

        etSearch = findViewById(R.id.et_search);
        TextView tvVersion = findViewById(R.id.tv_version);
        tvVersion.setText(String.format("版本：v%s", BuildConfig.VERSION_NAME));
        tvVersion.setOnClickListener(this);
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
            case R.id.tv_version:
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("感谢");
                builder.setMessage("开源不易，给作者一个star好吗？😊");
                builder.setCancelable(false);
                builder.setNegativeButton("已给", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "感谢老铁~", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setPositiveButton("去star", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WebViewActivity.loadUrl(MainActivity.this, "https://github.com/youlookwhat/WebViewStudy", "WebViewStudy");
                    }
                });
                builder.show();
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
            // 空url
            url = "https://github.com/youlookwhat/WebViewStudy";

        } else if (!url.startsWith("http") && url.contains("http")) {
            // 有http且不在头部
            url = url.substring(url.indexOf("http"), url.length());

        } else if (url.startsWith("www")) {
            // 以"www"开头
            url = "http://" + url;

        } else if (!url.startsWith("http") && (url.contains(".me") || url.contains(".com") || url.contains(".cn"))) {
            // 不以"http"开头且有后缀
            url = "http://www." + url;

        } else if (!url.startsWith("http") && !url.contains("www")) {
            // 输入纯文字 或 汉字的情况
            url = "http://m5.baidu.com/s?from=124n&word=" + url;
        }
        WebViewActivity.loadUrl(this, url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionbar_update:
                WebViewActivity.loadUrl(this, "https://fir.im/webviewstudy", "网页浏览器 - fir.im");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
