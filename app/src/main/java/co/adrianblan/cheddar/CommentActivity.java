package co.adrianblan.cheddar;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Adrian on 2015-07-29.
 */
public class CommentActivity extends AppCompatActivity {

    CommentAdapter commentAdapter;
    ArrayList<Long> kids;
    ListView lv;

    Long submissionId;
    Long points;
    Long commentCount;
    Long newCommentCount;

    // Base URL for the hacker news API
    private Firebase baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Init API stuff
        Firebase.setAndroidContext(getApplicationContext());
        baseUrl = new Firebase("https://hacker-news.firebaseio.com/v0/item/");

        Bundle b = getIntent().getExtras();
        submissionId = b.getLong("submissionId");
        commentCount = b.getLong("commentCount");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_comment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(b.getString("title"));

        commentAdapter = new CommentAdapter(getApplicationContext());
        lv = (ListView) findViewById(R.id.activity_comment_list);
        lv.setAdapter(commentAdapter);
        lv.addHeaderView(initHeader(b));

        updateComments();
    }

    public View initHeader(Bundle bundle){

        final Bundle b = bundle;

        View header = View.inflate(getApplicationContext(), R.layout.feed_item, null);

        TextView title = (TextView) header.findViewById(R.id.feed_item_title);
        title.setText(b.getString("title"));

        TextView subtitle = (TextView) header.findViewById(R.id.feed_item_shortUrl);
        subtitle.setText(b.getString("shortUrl"));

        TextView score = (TextView) header.findViewById(R.id.feed_item_score);
        score.setText(Long.toString(b.getLong("score")));

        TextView comments = (TextView) header.findViewById(R.id.feed_item_comments);
        comments.setText(Long.toString(b.getLong("commentCount")));

        TextView time = (TextView) header.findViewById(R.id.feed_item_time);
        time.setText(b.getString("time"));

        Intent intent = getIntent();
        Bitmap thumbnail = (Bitmap) intent.getParcelableExtra("thumbnail");

        // Generate new TextDrawable
        ImageView im = (ImageView) header.findViewById(R.id.feed_item_thumbnail);
        if(thumbnail != null){
            im.setImageBitmap(thumbnail);
        } else {
            TextDrawable.IShapeBuilder builder = TextDrawable.builder().beginConfig().bold().toUpperCase().endConfig();
            TextDrawable drawable = builder.buildRect(b.getString("letter"), getApplicationContext().getResources().getColor(R.color.colorPrimary));
            im.setImageDrawable(drawable);
        }

        // If we click the thumbnail, get to the webview
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), WebViewActivity.class);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        return header;
    }

    public void addCommentCount(int count){
        newCommentCount += (long) count;

        if(newCommentCount > commentCount){
            commentCount = newCommentCount;

            //Update header
        }
    }

    // Starts updating the commentCount from the top level
    public void updateComments(){

        newCommentCount = 0L;
        commentAdapter.clear();

        baseUrl.child(Long.toString(submissionId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // We retrieve all objects into a hashmap
                Map<String, Object> ret = (Map<String, Object>) snapshot.getValue();
                kids = (ArrayList<Long>) ret.get("kids");

                if (kids != null) {

                    addCommentCount(kids.size());

                    //TODO fix race condition
                    for (int i = 0; i < kids.size(); i++) {
                        updateComment(kids.get(i), null);
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
    public void updateComment(Long id, Comment parent){

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
                com.setTitle((String) ret.get("by"));
                com.setBody((String) ret.get("text"));

                Date past = new Date((Long) ret.get("time") * 1000);
                Date now = new Date();
                com.setTime(getPrettyDate(past, now));

                // Check if top level comment
                if(par == null) {
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

                    addCommentCount(kids.size());

                    // We're counting backwards since we are too lazy to fix a race condition
                    //TODO fix race condition
                    for (int i = 1; i <= kids.size(); i++) {
                        updateComment(kids.get(kids.size() - i), com);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.err.println("Could not retrieve post! " + firebaseError);
            }
        });
    }

    // Converts the difference between two dates into a pretty date
    // There's probably a joke in there somewhere
    public String getPrettyDate(Date past, Date now){

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
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.refresh) {
            updateComments();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
}
