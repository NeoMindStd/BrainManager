package std.neomind.brainmanager;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WebViewActivity extends AppCompatActivity {
    private static final String TAG = "WebViewActivity";

    public static final String title = "TITLE";
    public static final String tag = "URL";
    
    private Toolbar toolbarPrivacyPolicy;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Initialize();
    }

    private void Initialize() {
        Intent intent = getIntent();

        // Toolbar
        toolbarPrivacyPolicy = findViewById(R.id.toolbarWebView);
        setSupportActionBar(toolbarPrivacyPolicy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(intent.getStringExtra("TITLE"));

        // Web View
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(intent.getStringExtra("URL"));
    }
}