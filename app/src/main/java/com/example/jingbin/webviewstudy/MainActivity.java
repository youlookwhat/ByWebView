package com.example.jingbin.webviewstudy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.bt_baidu)
    Button btBaidu;
    @BindView(R.id.bt_call)
    Button btCall;
    @BindView(R.id.bt_upload_photo)
    Button btUploadPhoto;
    @BindView(R.id.bt_movie)
    Button btMovie;
    @BindView(R.id.bt_js)
    Button btJs;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        btBaidu.setOnClickListener(this);
        btCall.setOnClickListener(this);
        btUploadPhoto.setOnClickListener(this);
        btMovie.setOnClickListener(this);
        btJs.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_baidu:// 百度一下
                String baiduUrl = "http://www.baidu.com";
                WebViewActivity.loadUrl(this, baiduUrl);
                break;
            case R.id.bt_call:
                String callUrl = "https://v4-stage-api.kangaiweishi.com/v4/articles/fa1ffcd611934e80bb6e490bed15efb8.html";
                WebViewActivity.loadUrl(this, callUrl);
                break;
            case R.id.bt_upload_photo:
                String uploadUrl = "http://taoyanran.duapp.com/kaws/salvation/salvation.html";
                WebViewActivity.loadUrl(this, uploadUrl);
                break;
            case R.id.bt_movie:
                // 优酷链接跳到浏览器
//                String movieUrl = "http://v.youku.com/v_show/id_XNzMxNzUyNzQ0.html?beta&#paction";
                // 费玉清呜呜呜
//                String movieUrl = "http://www.tudou.com/albumplay/eu0K8vLTD48/aHeFLTBfzU0.html";
                // 可全屏
                String movieUrl = "http://player.youku.com/embed/XMTMxOTk1ODI4OA";
                WebViewActivity.loadUrl(this, movieUrl);
                break;
            case R.id.bt_js:
                String jsUrl = "https://v4-stage-api.kangaiweishi.com/v4/articles/ca73922b2aca433f8541698e71bb95c0.html";
                WebViewActivity.loadUrl(this, jsUrl);

                break;
            default:
                break;
        }
    }
}
