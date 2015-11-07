package co.adrianblan.cheddar.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.devspark.robototextview.widget.RobotoTextView;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import co.adrianblan.cheddar.R;
import co.adrianblan.cheddar.models.Comment;
import co.adrianblan.cheddar.models.FeedItem;
import co.adrianblan.cheddar.utils.StringUtils;
import co.adrianblan.cheddar.views.JellyBeanCompatTextView;
import co.adrianblan.cheddar.views.adapters.CommentAdapter;

import static android.widget.AdapterView.GONE;
import static android.widget.AdapterView.VISIBLE;

// Activity which shows comments to a feed item
public class CommentActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeContainer;
    private CommentAdapter commentAdapter; // Adapter which stores comments
    private ArrayList<Comment> comments; // Reference to adapter dataset
    private ArrayList<Long> kids; // Stores the array of individual comment IDs
    private Date lastSubmissionUpdate; // Time since last submission update


    private FeedItem feedItem;
    private Long newCommentCount;
    private Bitmap thumbnail;

    private View header;
    private View no_comments;
    private View progress;

    // Base URL for the Hacker News Firebase API
    private Firebase baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Init API stuff
        Firebase.setAndroidContext(this);
        baseUrl = new Firebase("https://hacker-news.firebaseio.com/v0/item/");

        if (savedInstanceState == null) {
            Bundle b = getIntent().getExtras();
            feedItem = b.getParcelable("feedItem");
            comments = new ArrayList<>();
        } else {
            // We retrieve the saved items
            feedItem = savedInstanceState.getParcelable("feedItem");
            comments = savedInstanceState.getParcelableArrayList("comments");

        }
        commentAdapter = new CommentAdapter(comments, this, feedItem);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.comment_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(commentAdapter);


        // Init toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_comment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(feedItem.getTitle());

        no_comments = findViewById(R.id.activity_comment_none);
        progress = findViewById(R.id.activity_comment_progress);

        // Don't update if we already have retrieved saved comments
        if (comments.isEmpty()) {
            updateComments();
        }

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateComments();
            }
        });

    }

//    // Adds onClickListeners for hiding and revealing comments
//    public void addCommentOnClickListeners(ListView lv) {
//
//        // So, for some weird reason our longClicks are not consumed properly
//        // Thus we need a manual timeout to prevent onItemClick from triggering after onItemLongClick
//        lastOnItemLongClick = new Date();
//
//        // And short clicks for revealing comments
//        lv.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                // If there is no comment data, or we clicked the header, return
//                if (commentAdapter.getComments().size() == 0 || position == 0) {
//                    return;
//                }
//
//                Date d = new Date();
//                long ms = (d.getTime() - lastOnItemLongClick.getTime());
//
//                Comment comment = commentAdapter.getItem(position - 1);
//                int hierarchy = comment.getHierarchy();
//
//                if (comment.hasHideChildren() && ms > 1500) {
//
//                    comment.setHideChildren(false);
//                    // Find all child comments with higher hierarchy and show them
//                    for (int i = position; commentAdapter.getComments().get(i).getHierarchy() > hierarchy && i < commentAdapter.getComments().size(); i++) {
//                        commentAdapter.getItem(i).setIsHidden(false);
//                    }
//                }
//                commentAdapter.notifyDataSetChanged();
//            }
//        });
//
//        // We listen to long clicks for hiding comments
//        lv.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//                // If there is no comment data, or we clicked the header, return
//                if (commentAdapter.getComments().size() == 0 || position == 0) {
//                    return false;
//                }
//
//                Comment comment = commentAdapter.getItem(position - 1);
//                int hierarchy = comment.getHierarchy();
//
//                if (!comment.hasHideChildren()) {
//                    lastOnItemLongClick = new Date();
//                    int i;
//
//                    // Find all child comments with higher hierarchy and hide them
//                    // We do the direct access since we want to be able to access hidden comments
//                    for (i = position; commentAdapter.getComments().get(i).getHierarchy() > hierarchy && i < commentAdapter.getComments().size(); i++) {
//                        commentAdapter.getItem(i).setIsHidden(true);
//                    }
//
//                    int hiddenChildren = i - position;
//                    comment.setHiddenChildren(hiddenChildren);
//
//                    if (hiddenChildren > 0) {
//                        comment.setHideChildren(true);
//                    }
//
//                    commentAdapter.notifyDataSetChanged();
//                }
//
//                return true;
//            }
//        });
//    }

    // Starts updating the commentCount from the top level
    public void updateComments() {

        if (lastSubmissionUpdate != null) {
            Date d = new Date();
            long seconds = (d.getTime() - lastSubmissionUpdate.getTime()) / 1000;

            // We want to throttle repeated refreshes
            if (seconds < 2) {
                resetSwipeContainer();
                return;
            }
        }

        lastSubmissionUpdate = new Date();

        newCommentCount = 0L;
        comments.clear();
        commentAdapter.notifyDataSetChanged();

        progress.setVisibility(View.VISIBLE);
        no_comments.setVisibility(View.GONE);

        // We retrieve the comment data belonging to the feed item
        baseUrl.child(Long.toString(feedItem.getSubmissionId())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // Hide the progress bar
                progress.setVisibility(View.GONE);

                // We retrieve all objects into a hashmap
                Map<String, Object> ret = (Map<String, Object>) snapshot.getValue();
                kids = (ArrayList<Long>) ret.get("kids");

                // Update the feed item data
                feedItem.setTime(StringUtils.getPrettyDate((long) ret.get("time")));
                feedItem.setScore((Long) ret.get("score"));

                // If the feed item has comments
                if (kids != null) {

                    no_comments.setVisibility(View.GONE);
                    newCommentCount += kids.size();
//                    updateHeader();

                    //TODO fix race condition
                    for (int i = 0; i < kids.size(); i++) {
                        updateSingleComment(kids.get(i), null);
                    }
                } else {
//                    updateHeader();

                    //If we can't load any posts, we show a warning
                    no_comments.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.err.println("Could not retrieve post! " + firebaseError);
                no_comments.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        });

        resetSwipeContainer();
    }

    // Stops the swipe container refresh animation
    public void resetSwipeContainer() {
        if (swipeContainer != null) {
            swipeContainer.setRefreshing(false);
        }
    }

    // Gets an url to a single comment
    public void updateSingleComment(Long id, Comment parent) {

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
                com.setTime(StringUtils.getPrettyDate((Long) ret.get("time")));

                // Check if top level comment
                if (par == null) {
                    com.setHierarchy(0);
                    comments.add(com);
                } else {
                    com.setHierarchy(par.getHierarchy() + 1);
                    comments.add(comments.indexOf(par) + 1, com);
                }

//                // If we load a comment into a collapsed chain, we must hide it
//                int position = comments.indexOf(com);
//                if (position > 0) {
//
//                    Comment aboveComment = comments.get(position - 1);
//
//                    if (aboveComment.isHidden() || (aboveComment.hasHideChildren() && aboveComment.getHierarchy() > com.getHierarchy())) {
//                        com.setIsHidden(true);
//                    }
//                }
                commentAdapter.notifyItemChanged(comments.indexOf(com));
                ArrayList<Long> kids = (ArrayList<Long>) ret.get("kids");

                // Update child commentCount
                if (kids != null) {

                    newCommentCount += kids.size();
//                    updateHeader();

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
    public void updateHeader() {
        TextView scoreView = (TextView) header.findViewById(R.id.feed_item_score);
        scoreView.setText(Long.toString(feedItem.getScore()));

        // If the number of new comments is larger than the old number
        if(newCommentCount > feedItem.getDescendants()) {
            TextView commentView = (TextView) header.findViewById(R.id.feed_item_comments);
            commentView.setText(Long.toString(newCommentCount));
            feedItem.setDescendants(newCommentCount);
        }

        TextView timeView = (TextView) header.findViewById(R.id.feed_item_time);
        timeView.setText(feedItem.getTime());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        // If we don't have an Url, we make sure you can't go to the webview
        if (feedItem.getLongUrl() != null) {
            menuInflater.inflate(R.menu.menu_comments, menu);
        } else {
            menuInflater.inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.menu_refresh) {
            updateComments();
        } else if (id == R.id.menu_webview) {
            Intent intent = new Intent(this, WebViewActivity.class);
            Bundle b = new Bundle();
            b.putParcelable("feedItem", feedItem);
            intent.putExtra("thumbnail", thumbnail);
            intent.putExtras(b);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Save data
        savedInstanceState.putParcelable("feedItem", feedItem);
        savedInstanceState.putParcelableArrayList("comments", comments);
    }
}
