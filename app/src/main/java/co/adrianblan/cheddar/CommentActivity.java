package co.adrianblan.cheddar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.net.MalformedURLException;
import java.net.URL;
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
        kids = (ArrayList<Long>) b.getSerializable("kids");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_comment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(b.getString("title"));

        // TODO put menu items in toolbar
        //inflater.inflate(R.menu.menu_main, menu);

        commentAdapter = new CommentAdapter();
        ListView lv = (ListView) findViewById(R.id.activity_comment_list);
        lv.setAdapter(commentAdapter);

        for(int i = 0; i < kids.size(); i++){
            updateComment(baseUrl.child(Long.toString(kids.get(i))));
        }
    }

    // Gets an url to a single comment
    public void updateComment(Firebase submission){

        submission.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // We retrieve all objects into a hashmap
                Map<String, Object> ret = (Map<String, Object>) snapshot.getValue();

                if(ret == null){
                    return;
                }

                Comment c = new Comment();
                c.setTitle((String) ret.get("by"));
                c.setBody((String) ret.get("text"));

                Date past = new Date((Long) ret.get("time") * 1000);
                Date now = new Date();
                c.setTime(getPrettyDate(past, now));

                commentAdapter.add(c);
                commentAdapter.notifyDataSetChanged();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.refresh) {
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
}
