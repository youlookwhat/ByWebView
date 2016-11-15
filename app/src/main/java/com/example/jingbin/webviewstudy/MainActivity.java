package com.example.jingbin.webviewstudy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.bt_webview)
    Button btWebview;
    @BindView(R.id.activity_main)
    RelativeLayout activityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btWebview.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_webview:
                // 上传图片
//                String url = "http://taoyanran.duapp.com/kaws/salvation/salvation.html";
//                String url = "http://www.baidu.com";
//                String url = "http://support.kangaiweishi.com/articles/fa1ffcd611934e80bb6e490bed15efb8";
                // js查看图片
//                String url = "https://v4-stage-api.kangaiweishi.com/v4/articles/5c528993bd394f989d3e5dddef6a7bbf.html";
                // 电话,邮件,短信、js查看图片
//                String url = "https://v4-stage-api.kangaiweishi.com/v4/articles/fa1ffcd611934e80bb6e490bed15efb8.html";
                // 点击跳转
                String url="https://v4-stage-api.kangaiweishi.com/v4/articles/ca73922b2aca433f8541698e71bb95c0.html";
                WebViewActivity.loadUrl(this,url);
//                LoadImageWebviewActivity.view(this);
                break;
            default:
                break;
        }
    }
}
