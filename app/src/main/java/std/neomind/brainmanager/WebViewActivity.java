package std.neomind.brainmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WebViewActivity extends AppCompatActivity {
    public static final String EXTRAS_TITLE = "title";
    public static final String EXTRAS_URL = "url";
    private static final String TAG = "WebViewActivity";
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
        toolbarPrivacyPolicy = findViewById(R.id.webView_toolbar);
        setSupportActionBar(toolbarPrivacyPolicy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(intent.getStringExtra(EXTRAS_TITLE));

        // Web View
        webView = findViewById(R.id.webView_webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(intent.getStringExtra(EXTRAS_URL));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) onBackPressed();

        return true;
    }
}