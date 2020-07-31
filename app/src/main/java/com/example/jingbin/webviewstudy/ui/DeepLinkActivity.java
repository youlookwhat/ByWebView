package com.example.jingbin.webviewstudy.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.jingbin.webviewstudy.R;

import java.util.List;

/**
 * 测试DeepLink打开页面
 */
public class DeepLinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deep_link);
        TextView textView = (TextView) findViewById(R.id.tv_deeplink);
        getDataFromBrowser(textView);
    }

    /**
     * 从deep link中获取数据
     * 'will://share/传过来的数据'
     */
    private void getDataFromBrowser(TextView textView) {
        Uri data = getIntent().getData();
        try {
            String scheme = data.getScheme();
            String host = data.getHost();
            List<String> params = data.getPathSegments();
            // 从网页传过来的数据
            String testId = params.get(0);
            String text = "Scheme: " + scheme + "\n" + "host: " + host + "\n" + "params: " + testId;
            Log.e("ScrollingActivity", text);
            textView.setText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
