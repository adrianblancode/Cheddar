package co.adrianblan.cheddar.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import co.adrianblan.cheddar.models.FeedItem;
import co.adrianblan.cheddar.R;

public class WebViewActivity extends AppCompatActivity {

    private WebView myWebView;
    private ProgressBar progressBar;
    private FeedItem feedItem;
    private Bitmap thumbnail;
    private boolean hasFeedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Bundle b = getIntent().getExtras();
        feedItem = b.getParcelable("feedItem");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_webview);
        setSupportActionBar(toolbar);

        myWebView = (WebView) findViewById(R.id.webview);

        // Load previous state if we have it
        if(savedInstanceState != null){
            myWebView.restoreState(savedInstanceState);
        } else {
            initWebView(myWebView);
        }

        // If we have a related feed item, we choose the advanced toolbar
        if(feedItem != null){
            hasFeedItem = true;
            thumbnail = getIntent().getParcelableExtra("thumbnail");
            getSupportActionBar().setTitle(feedItem.getTitle());
            getSupportActionBar().setSubtitle(feedItem.getShortUrl());

            // If we don't have previous state, reload the URL
            if(savedInstanceState == null) {
                myWebView.loadUrl(feedItem.getLongUrl());
            }
        } else {
            hasFeedItem = false;
            getSupportActionBar().setTitle(b.getString("url"));

            if(savedInstanceState == null) {
                myWebView.loadUrl(b.getString("url"));
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
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

            @Override
            public void onReceivedTitle(WebView view, String title) {

                if(!hasFeedItem) {
                    // When the site title loads, we change the view title also
                    super.onReceivedTitle(view, title);
                    if (!TextUtils.isEmpty(title)) {
                        // The old title becomes the new subtitle
                        getSupportActionBar().setSubtitle(getSupportActionBar().getTitle());
                        getSupportActionBar().setTitle(title);
                    }
                }
            }

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
        // We need different menus depending on if we have a related feed item or not
        if(hasFeedItem) {
            getMenuInflater().inflate(R.menu.menu_webview, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    // Save the state of the web view when the screen is rotated.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        myWebView.saveState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }

        else if(id == R.id.menu_refresh) {
            myWebView.reload();
        }

        // This button will only appear if we have a feed item
        else if(id == R.id.menu_comments && hasFeedItem){

            // Go to the comments
            Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
            Bundle b = new Bundle();
            b.putParcelable("feedItem", feedItem);
            intent.putExtra("thumbnail", thumbnail);
            intent.putExtras(b);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
