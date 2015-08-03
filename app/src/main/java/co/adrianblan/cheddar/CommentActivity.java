package co.adrianblan.cheddar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Adrian on 2015-07-29.
 */
public class CommentActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

    CommentAdapter commentAdapter;
    ArrayList<Long> kids;
    Date lastSubmissionUpdate;

    FeedItem feedItem;
    Long newCommentCount;
    Bitmap thumbnail;

    View header;

    // Base URL for the hacker news API
    private Firebase baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Init API stuff
        Firebase.setAndroidContext(this);
        baseUrl = new Firebase("https://hacker-news.firebaseio.com/v0/item/");

        Bundle b = getIntent().getExtras();
        feedItem = (FeedItem) b.getSerializable("feedItem");

        if(feedItem == null){
            System.err.println("Passed null arguments into CommentActivity!");
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_comment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(feedItem.getTitle());

        commentAdapter = new CommentAdapter(this, feedItem.getBy());
        ObservableListView lv = (ObservableListView) findViewById(R.id.activity_comment_list);
        lv.setScrollViewCallbacks(this);
        lv.setAdapter(commentAdapter);
        lv.addHeaderView(initHeader(feedItem));

        updateComments();
    }

    // Initializes the submission header with data
    public View initHeader(final FeedItem feedItem){

        header = View.inflate(this, R.layout.feed_item, null);

        TextView title = (TextView) header.findViewById(R.id.feed_item_title);
        title.setText(feedItem.getTitle());

        TextView subtitle = (TextView) header.findViewById(R.id.feed_item_shortUrl);
        subtitle.setText(feedItem.getShortUrl());

        TextView score = (TextView) header.findViewById(R.id.feed_item_score);
        score.setText(Long.toString(feedItem.getScore()));

        TextView comments = (TextView) header.findViewById(R.id.feed_item_comments);
        comments.setText(Long.toString(feedItem.getCommentCount()));

        TextView time = (TextView) header.findViewById(R.id.feed_item_time);
        time.setText(feedItem.getTime());

        thumbnail = (Bitmap) getIntent().getParcelableExtra("thumbnail");

        // Generate new TextDrawable
        ImageView imageView = (ImageView) header.findViewById(R.id.feed_item_thumbnail);
        if(thumbnail != null){
            imageView.setImageBitmap(thumbnail);
        } else {
            TextDrawable.IShapeBuilder builder = TextDrawable.builder().beginConfig().bold().toUpperCase().endConfig();
            TextDrawable drawable = builder.buildRect(feedItem.getLetter(), feedItem.getColor());
            imageView.setImageDrawable(drawable);
        }

        if(feedItem.getShortUrl() != null){
            LinearLayout image_container = (LinearLayout) header.findViewById(R.id.feed_item_thumbnail_container);
            // If we click the thumbnail, get to the webview
            image_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), WebViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("feedItem", feedItem);
                    intent.putExtra("thumbnail", thumbnail);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        //Add padding so that we compensate for the Toolbar
        updateHeaderPadding(true);

        return header;
    }

    // Starts updating the commentCount from the top level
    public void updateComments(){

        if(lastSubmissionUpdate != null) {
            Date d = new Date();
            long seconds = (d.getTime() - lastSubmissionUpdate.getTime()) / 1000;

            // We want to throttle repeated refreshes
            if (seconds < 2) {
                return;
            }
        }

        lastSubmissionUpdate = new Date();

        newCommentCount = 0L;
        commentAdapter.clear();
        commentAdapter.notifyDataSetChanged();

        baseUrl.child(Long.toString(feedItem.getSubmissionId())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // We retrieve all objects into a hashmap
                Map<String, Object> ret = (Map<String, Object>) snapshot.getValue();
                kids = (ArrayList<Long>) ret.get("kids");

                feedItem.setTime(getPrettyDate((long) ret.get("time")));
                feedItem.setScore((Long) ret.get("score"));

                if (kids != null) {

                    newCommentCount += kids.size();
                    updateHeader();

                    //TODO fix race condition
                    for (int i = 0; i < kids.size(); i++) {
                        updateSingleComment(kids.get(i), null);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.err.println("Could not retrieve post! " + firebaseError);
            }
        });
    }

    // Gets an url to a single comment
    public void updateSingleComment(Long id, Comment parent){

        final Comment par = parent;

        baseUrl.child(Long.toString(id)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // We retrieve all objects into a hashmap
                Map<String, Object> ret = (Map<String, Object>) snapshot.getValue();

                if (ret == null || ret.get("text") == null) {
                    return;
                }

                Comment com = new Comment();
                com.setBy((String) ret.get("by"));
                com.setBody((String) ret.get("text"));
                com.setTime(getPrettyDate((Long) ret.get("time")));

                // Check if top level comment
                if (par == null) {
                    com.setHierarchy(0);
                    commentAdapter.add(com);
                } else {
                    com.setHierarchy(par.getHierarchy() + 1);
                    commentAdapter.add(commentAdapter.getPosition(par) + 1, com);
                }
                commentAdapter.notifyDataSetChanged();
                ArrayList<Long> kids = (ArrayList<Long>) ret.get("kids");

                // Update child commentCount
                if (kids != null) {

                    newCommentCount += kids.size();
                    updateHeader();

                    // We're counting backwards since we are too lazy to fix a race condition
                    //TODO fix race condition
                    for (int i = 1; i <= kids.size(); i++) {
                        updateSingleComment(kids.get(kids.size() - i), com);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.err.println("Could not retrieve post! " + firebaseError);
            }
        });
    }

    // Updates the header with new data
    public void updateHeader(){
        TextView scoreView = (TextView) header.findViewById(R.id.feed_item_score);
        scoreView.setText(Long.toString(feedItem.getScore()));

        if(newCommentCount > feedItem.getCommentCount()) {
            TextView commentView = (TextView) header.findViewById(R.id.feed_item_comments);
            commentView.setText(Long.toString(newCommentCount));
            feedItem.setCommentCount(newCommentCount);
        }

        TextView timeView = (TextView) header.findViewById(R.id.feed_item_time);
        timeView.setText(feedItem.getTime());
    }

    // Converts the difference between two dates into a pretty date
    // There's probably a joke in there somewhere
    public String getPrettyDate(Long time){

        Date past = new Date(time * 1000);
        Date now = new Date();

        if(TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) > 0){
            return TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + "d";
        } else if(TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) > 0){
            return TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + "h";
        } if(TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) > 0){
            return TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + "m";
        } else {
            return TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime()) + "s";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_comments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_refresh) {
            updateComments();
        }

        if(id == R.id.menu_webview){
            Intent intent = new Intent(this, WebViewActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("feedItem", feedItem);
            intent.putExtra("thumbnail", thumbnail);
            intent.putExtras(b);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // We do nothing here. We're only handling this to keep orientation
        // or keyboard hiding from causing the WebView activity to restart.
        // Yes, this is terrible
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {}

    @Override
    public void onDownMotionEvent() {}

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = getSupportActionBar();
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ab.hide();
                updateHeaderPadding(false);
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
                updateHeaderPadding(true);
            }
        }
    }

    // Updates the padding on header to compensate for what is visible on the screen
    public void updateHeaderPadding(boolean show){
        ActionBar ab = getSupportActionBar();
        if (show) {
            header.setPadding(0, (int) getResources().getDimension(R.dimen.toolbar_height), 0, 0);
        } else {
            // If we hide the toolbar, we need to reduce the padding to compensate
            header.setPadding(0, 0, 0, 0);
        }
    }
}
