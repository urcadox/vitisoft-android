package vitisoft.vitisoftapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String url = intent.getStringExtra(Consts.URL_MESSAGE);
        WebView webview = new WebView(this);
        setContentView(webview);
        webview.loadUrl(url);
    }
}
