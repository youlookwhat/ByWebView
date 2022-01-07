package com.example.jingbin.webviewstudy.ui;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
     * 'scheme://host/path?传过来的数据' 示例：will://link/testId?type=1&id=345
     */
    private void getDataFromBrowser(TextView textView) {
        Uri data = getIntent().getData();
        try {
            String scheme = data.getScheme();
            String host = data.getHost();
            String path = data.getPath();
            // 从网页传过来的数据
            String query = data.getQuery();
            String text = "scheme: " + scheme + "\n" + "host: " + host + "\n" + "path: " + path + "\n" + "query: " + query;
            Log.e("ScrollingActivity", text);
            textView.setText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
