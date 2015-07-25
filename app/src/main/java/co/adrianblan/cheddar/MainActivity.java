package co.adrianblan.cheddar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FeedAdapter feedAdapter = new FeedAdapter();
    Firebase baseURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        baseURL = new Firebase("https://hacker-news.firebaseio.com/v0/");
        final Firebase topStoriesURL = baseURL.child("/topstories");

        topStoriesURL.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                ArrayList<Long> ret = (ArrayList<Long>) snapshot.getValue();

                for(int i = 0; i < 20; i++) {

                    Firebase storyURL = baseURL.child("/item/" + ret.get(i));
                    storyURL.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            FeedItem f = new FeedItem();

                            Map<String, Object> ret = (Map<String, Object>) snapshot.getValue();
                            f.setTitle((String) ret.get("title"));
                            f.setSubtitle1((String) ret.get("by"));
                            f.setSubtitle2(Long.toString((Long) ret.get("score")));

                            feedAdapter.add(f);
                            feedAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            System.err.println("Could not retrieve post! " + firebaseError);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.err.println("Could not retrieve posts! " + firebaseError);
            }
        });

        ListView listView = (ListView) findViewById(R.id.feed);
        listView.setAdapter(feedAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
