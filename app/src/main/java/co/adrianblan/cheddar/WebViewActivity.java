package co.adrianblan.cheddar;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by Adrian on 2015-07-28.
 */
public class WebViewActivity extends AppCompatActivity {

    private WebView myWebView;
    private ProgressBar progressBar;
    private FeedItem feedItem;
    private Bitmap thumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Bundle b = getIntent().getExtras();
        feedItem = (FeedItem) b.getSerializable("feedItem");
        thumbnail = (Bitmap) getIntent().getParcelableExtra("thumbnail");

        if(feedItem == null){
            System.err.println("Passed null arguments into WebViewActivity!");
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_webview);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(feedItem.getTitle());
        getSupportActionBar().setSubtitle(feedItem.getShortUrl());

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);

        myWebView = (WebView) findViewById(R.id.webview);
        initWebView(myWebView);
        myWebView.loadUrl(feedItem.getLongUrl());

    }

    // Initializes the WebView with settings, onclick etc
    public void initWebView(WebView myWebView){
        myWebView.getSettings().setJavaScriptEnabled(true);

        // We want to start the page zoomed out
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setUseWideViewPort(true);

        // We also need to support pinch zoom
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(false);

        //Enable progress bar
        myWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);

                //Hide and show progressbar based on percent
                if(progressBar.getProgress() < 100){
                    progressBar.setVisibility(View.VISIBLE);
                } else if(progressBar.getProgress() == 100){
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        //Prevent that the browser leaves the view
        myWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_webview, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.menu_refresh) {
            myWebView.reload();
        }

        if(id == R.id.menu_comments){
            Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("feedItem", feedItem);
            intent.putExtra("thumbnail", thumbnail);
            intent.putExtras(b);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
