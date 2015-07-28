package co.adrianblan.cheddar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private FeedAdapter feedAdapter = new FeedAdapter();
    private Firebase baseUrl;
    private Firebase topStoriesUrl;
    private boolean initialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!initialized) {
            initialized = true;
            initImageLoader();
            Firebase.setAndroidContext(this);
            baseUrl = new Firebase("https://hacker-news.firebaseio.com/v0/");
            topStoriesUrl = baseUrl.child("/topstories");
        }

        ListView listView = (ListView) findViewById(R.id.feed);
        listView.setAdapter(feedAdapter);

        // Gets all the submissions and populates the list with them
        if(feedAdapter.getCount() == 0) {
            updateSubmissions();
        }
    }

    // Fetches a large number of submissions, and updates them individually
    public void updateSubmissions(){
        topStoriesUrl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // List of submission IDs
                ArrayList<Long> ret = (ArrayList<Long>) snapshot.getValue();

                // From the top 500 submissions, we only display a few
                for (int i = 0; i < 25; i++) {

                    // But we must first add each submission to the view manually
                    updateSingleSubmission(baseUrl.child("/item/" + ret.get(i)));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.err.println("Could not retrieve posts! " + firebaseError);
            }
        });
    }

    // Gets an url to a single submission and updates it in the feedadapter
    public void updateSingleSubmission(Firebase submission){

        submission.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // We retrieve all objects into a hashmap
                Map<String, Object> ret = (Map<String, Object>) snapshot.getValue();

                URL site = null;
                try {
                    site = new URL((String) ret.get("url"));
                } catch (MalformedURLException e) {
                    System.err.println(e);
                    return;
                }

                FeedItem f = initNewFeedItem(ret, site);
                feedAdapter.add(f);
                feedAdapter.notifyDataSetChanged();

                // Asynchronously updates images for the feed item
                updateSubmissionThumbnail(site.getHost(), feedAdapter.getPosition(f));
                updateSubmissionFavicon(site.getHost(), feedAdapter.getPosition(f));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.err.println("Could not retrieve post! " + firebaseError);
            }
        });
    }

    // Takes the raw API data and the URL, returns a new feed item
    public FeedItem initNewFeedItem(Map<String, Object> ret, URL site){

        FeedItem f = new FeedItem();

        // Gets readable date
        Date past = new Date((Long) ret.get("time") * 1000);
        Date now = new Date();
        String time = getPrettyDate(past, now);

        // Set titles and other data
        f.setTitle((String) ret.get("title"));
        f.setSubtitle2(Long.toString((Long) ret.get("score")) + " points \u2022 0 comments \u2022 " + time);

        String domain = site.getHost().replace("www.", "");
        f.setSubtitle1(domain);

        // We show the first letter of the url on the thumbnail
        f.setLetter(Character.toString(domain.charAt(0)));

        return f;
    }

    // Recieves a host url, and the position of the feed item
    // Updates a high resolution thumbnail for that submission
    // Warning: using only the host url and not the full url
    public void updateSubmissionThumbnail(String url, int pos){

        final int position = pos;

        class DownloadFilesTask extends AsyncTask<String, Integer, String> {

            @Override
            protected String doInBackground(String... url) {
                // Gets url to a good thumbnail from a site
                return ThumbnailSearcher.getThumbnailUrl(url[0]);
            }
            @Override
            protected void onProgressUpdate(Integer... i) {
            }

            @Override
            protected void onPostExecute(String thumbnailUrl) {

                // Loads high resolution icon
                ImageLoader imageLoader = ImageLoader.getInstance();

                imageLoader.loadImage(thumbnailUrl, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String url, View view, Bitmap loadedImage) {

                        if(loadedImage == null){
                            return;
                        }

                        feedAdapter.getItem(position).setThumbnail(loadedImage);
                        feedAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        // Takes the above class and executes it asynchronously
        new DownloadFilesTask().execute("http://icons.better-idea.org/api/icons?url=" + url);
    }

    // Recieves a host url, and the position of the feed item
    // Updates a low resolution thumbnail for that submission
    public void updateSubmissionFavicon(String url, int pos){

        final int position = pos;
        ImageLoader imageLoader = ImageLoader.getInstance();

        // Loads low resolution favicon
        imageLoader.loadImage("http://www.google.com/s2/favicons?domain=" + url, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String url, View view, Bitmap loadedImage) {

                //We don't want to show the default image
                if (hashBitmap(loadedImage) == 56355950541L) {
                    return;
                }

                feedAdapter.getItem(position).setFavicon(loadedImage);
                feedAdapter.notifyDataSetChanged();
            }
        });
    }

    // Converts the difference between two dates into a pretty date
    // There's probably a joke in there somewhere
    public String getPrettyDate(Date past, Date now){

        if(TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) > 0){
            return TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + " days";
        } else if(TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) > 0){
            return TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + " hrs";
        } if(TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) > 0){
            return TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + " mins";
        } else {
            return TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime()) + " sec";
        }
    }

    //This is the shittiest hash ever, but we only need one image so
    public long hashBitmap(Bitmap bmp){

        long hash = 0;
        for(int x = 0; x < bmp.getWidth(); x++){
            for (int y = 0; y < bmp.getHeight(); y++){
                hash += Math.abs(bmp.getPixel(x,y));
            }
        }
        return hash;
    }

    // Initializes the image loader with what is probably reasonable values
    public void initImageLoader(){
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(getApplicationContext());
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(5 * 1024 * 1024); // 5 MiB
        config.tasksProcessingOrder(QueueProcessingType.FIFO);

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
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

    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
    }
}
