package co.adrianblan.cheddar.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import com.amulyakhare.textdrawable.TextDrawable;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import co.adrianblan.cheddar.views.adapters.FeedAdapter;
import co.adrianblan.cheddar.R;
import co.adrianblan.cheddar.activities.MainActivity;
import co.adrianblan.cheddar.models.FeedItem;

public class FeedFragment extends Fragment implements ObservableScrollViewCallbacks {

    private FeedAdapter feedAdapter;

    // Stores the submission IDs used for the API
    private ArrayList<Long> submissionIDs;

    // Base URL for the hacker news API
    private Firebase baseUrl;

    // Sub URL used for different stories
    private Firebase storiesUrl;

    // Collection of AsyncTasks we use to keep them from overflowing
    private ArrayList<AsyncTask> asyncTasks;

    //Throttle submissions
    private Date lastSubmissionUpdate;
    private final int submissionUpdateTime = 0;
    private final int submissionUpdateNum = 20;
    int loadedSubmissions = -1;

    View no_submissions;
    View progress;
    ProgressBar footer;

    private SwipeRefreshLayout swipeContainer;

    public static FeedFragment newInstance() {
        FeedFragment f = new FeedFragment();
        Bundle b = new Bundle();
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Init API stuff
        Firebase.setAndroidContext(getActivity().getApplicationContext());
        baseUrl = new Firebase("https://hacker-news.firebaseio.com/v0/");
        storiesUrl = baseUrl.child(getArguments().getString("url"));

        //noinspection Convert2Diamond
        asyncTasks = new ArrayList<>();
        lastSubmissionUpdate = new Date();

        if (savedInstanceState == null) {
            feedAdapter = new FeedAdapter(getActivity());
        } else {
            // Restore saved data
            ArrayList<FeedItem> feedItems = savedInstanceState.getParcelableArrayList("feedItems");
            feedAdapter = new FeedAdapter(feedItems, getActivity());
        }

        loadedSubmissions = feedAdapter.getCount();

    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        // We load only onstart since we need to edit view visibility in updateSubmissions()
        if (loadedSubmissions == 0) {
            updateSubmissions();
        }

        updateHeaderPadding();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        ObservableListView listView = (ObservableListView) rootView.findViewById(R.id.feed_list);
        listView.setScrollViewCallbacks(this);

        no_submissions = rootView.findViewById(R.id.activity_main_none);
        progress = rootView.findViewById(R.id.activity_main_progress);

        listView.setAdapter(feedAdapter);

        //If we scroll to the end, we simply fetch more submissions
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int preLast;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (view.getId() == R.id.feed_list) {
                    final int lastItem = firstVisibleItem + visibleItemCount;
                    if (lastItem >= totalItemCount) {

                        Date d = new Date();
                        long seconds = (d.getTime() - lastSubmissionUpdate.getTime()) / 1000;

                        //to avoid multiple calls for last item
                        if (lastItem != preLast && lastItem >= submissionUpdateNum && seconds >= submissionUpdateTime) {

                            // Get new feed items
                            updateSubmissions();
                            preLast = lastItem;
                            lastSubmissionUpdate = d;
                        }
                    }
                }
            }
        });

        // Show loading progress bar
        footer = new ProgressBar(getActivity().getApplicationContext());
        footer.setPadding(0, 65, 0, 65);
        footer.setIndeterminate(true);
        footer.setVisibility(View.GONE);
        listView.addFooterView(footer);

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetSubmissions();
            }
        });

        updateHeaderPadding(true);
        return rootView;
    }

    // Sometimes we just might want to reset all submissions
    public void resetSubmissions() {

        Date d = new Date();
        long seconds = (d.getTime() - lastSubmissionUpdate.getTime()) / 1000;

        //We dont want to be able to spam resets
        if (feedAdapter.getCount() > 0 && seconds >= submissionUpdateTime) {

            lastSubmissionUpdate = d;

            //First we need to cancel all asynctasks
            while (!asyncTasks.isEmpty()) {

                // Cancel all not finished tasks
                if (!asyncTasks.get(0).getStatus().equals(AsyncTask.Status.FINISHED)) {
                    asyncTasks.get(0).cancel(true);
                }

                asyncTasks.remove(0);
            }

            // We also cancel all image fetching
            ImageLoader.getInstance().stop();

            loadedSubmissions = 0;
            submissionIDs = null;
            feedAdapter.clear();
            feedAdapter.notifyDataSetChanged();

            progress.setVisibility(View.VISIBLE);
            footer.setVisibility(View.GONE);
            no_submissions.setVisibility(View.GONE);

            updateSubmissions();
            swipeContainer.setRefreshing(false);
        }
    }

    // Fetches a large number of submissions, and updates them individually
    public void updateSubmissions() {

        // If we don't have submissions loaded, we must first load them
        if (submissionIDs == null) {

            // Updates the list of 500 submission IDs
            storiesUrl.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    submissionIDs = (ArrayList<Long>) snapshot.getValue();

                    // Because we are doing this asynchronously, it's easier to update submissions directly
                    updateSubmissions();

                    // Hide the progress bar
                    progress.setVisibility(View.GONE);
                    footer.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.err.println("Could not retrieve posts! " + firebaseError);
                    no_submissions.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                    footer.setVisibility(View.GONE);
                }
            });
        } else {

            // We cannot use feedAdapter.getCount() directly since that may lead to race conditions
            int start = loadedSubmissions;

            // From the top 500 submissions, we only load a few at a time
            for (; loadedSubmissions < start + submissionUpdateNum && loadedSubmissions < submissionIDs.size(); loadedSubmissions++) {
                // But we must first add each submission to the view manually
                updateSingleSubmission(submissionIDs.get(loadedSubmissions));
            }

            if (loadedSubmissions == submissionIDs.size()) {
                no_submissions.setVisibility(View.VISIBLE);
            }
        }
    }

    // Gets an url to a single submission and updates it in the feedadapter
    public void updateSingleSubmission(final Long submissionId) {

        Firebase submission = baseUrl.child("/item/" + submissionId);

        submission.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                // We retrieve all objects into a hashmap
                Map<String, Object> ret = (Map<String, Object>) snapshot.getValue();

                if (ret == null) {
                    return;
                }

                String url = (String) ret.get("url");
                URL site = null;

                // If the url exists
                if (url != null) {
                    try {
                        site = new URL(url);
                    } catch (MalformedURLException e) {
                        System.err.println("Malformed url: " + url);
                    }
                }

                FeedItem f = initNewFeedItem(submissionId, ret, site);
                feedAdapter.add(f);
                feedAdapter.notifyDataSetChanged();

                if (site != null) {
                    // Asynchronously updates images for the feed item
                    updateSubmissionThumbnail(site.getHost(), f);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.err.println("Could not retrieve post! " + firebaseError);
            }
        });
    }

    // Takes the raw API data and the URL, returns a new feed item
    public FeedItem initNewFeedItem(Long submissionId, Map<String, Object> ret, URL site) {

        FeedItem f = new FeedItem();

        f.setSubmissionId(submissionId);

        // Gets readable date
        String time = getPrettyDate((Long) ret.get("time"));

        // Set titles and other data
        f.setTitle((String) ret.get("title"));
        f.setText((String) ret.get("text"));
        f.setBy((String) ret.get("by"));
        f.setScore((Long) ret.get("score"));
        f.setTime(time);

        // Jobs stories don't have any descendants, we need to take care of that
        Object descendantObject = ret.get("descendants");
        if (descendantObject != null) {
            f.setDescendants((Long) descendantObject);
        } else {
            System.err.println("Null descendants: " + ret.get("title"));
            f.setDescendants(0L);
        }

        // Hacker News site urls are null
        if (site != null) {
            String domain = site.getHost().replace("www.", "");
            f.setShortUrl(domain);
            f.setLongUrl(site.toString());
            f.setLetter(domain.substring(0, 1));
        } else {
            // The hacker news submissions don't technically have an url, so we cheat
            f.setShortUrl("Hacker News");
            f.setLetter("HN");
        }

        // Generate TextDrawable thumbnail
        TextDrawable.IShapeBuilder builder = TextDrawable.builder().beginConfig().bold().toUpperCase().endConfig();
        TextDrawable drawable = builder.buildRect(f.getLetter(), f.getColor());
        f.setTextDrawable(drawable);

        return f;
    }

    // Recieves a host url, and the position of the feed item
    // Fetches a remote server for the url to the best thumbnail to use
    public void updateSubmissionThumbnail(String url, FeedItem f) {

        final FeedItem fi = f;

        // Url to an API that automatically fetches the best thumbnail for the site
        String thumbnailUrl = "http://icons.better-idea.org/api/icons?url=" + url + "&i_am_feeling_lucky=yes";

        // For some weird reason, the ImageLoaderLibrary crashes if run below KitKat
        // Thus we instead have to do it manually with an AsyncTask
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            // Smarter way to async fetch a thumbnail
            SimpleImageLoadingListener thumbnailLoader = new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap thumbnail) {
                    processThumbnail(thumbnail, fi);
                }
            };

            ImageSize targetSize = new ImageSize(144, 144); // result Bitmap will be fit to this size
            ImageLoader.getInstance().loadImage(thumbnailUrl, targetSize, thumbnailLoader);
        } else {

            class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

                // Asynctask that fetches a thumbnail
                protected Bitmap doInBackground(String... urls) {
                    Bitmap b = null;
                    try {
                        InputStream in = new java.net.URL(urls[0]).openStream();
                        b = BitmapFactory.decodeStream(in);
                    } catch (Exception e) {
                        Log.e("Image fetching error", e.getMessage());
                        e.printStackTrace();
                    }
                    return b;
                }

                protected void onPostExecute(Bitmap thumbnail) {
                    processThumbnail(thumbnail, fi);
                }
            }

            DownloadImageTask task = new DownloadImageTask();
            asyncTasks.add(task);
            task.execute(thumbnailUrl);
        }
    }

    // Takes a thumbnail, and either places it or a TextDrawable for the item
    private void processThumbnail(Bitmap thumbnail, FeedItem f) {

        final FeedItem fi = f;

        int position = feedAdapter.getPosition(fi);

        if (thumbnail == null || position == -1) {
            return;
        }

        // We only display the image if it's large enough
        // Otherwise we create a TextDrawable for it
        if (thumbnail.getWidth() > 50 && thumbnail.getHeight() > 50) {
            feedAdapter.getItem(position).setThumbnail(thumbnail);
            feedAdapter.notifyDataSetChanged();
        }

        // Generate lots of palettes from the favicon asynchronously
        Palette.from(thumbnail).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {

                List<Palette.Swatch> swatches = p.getSwatches();
                Palette.Swatch vibrantSwatch = p.getVibrantSwatch();

                TextDrawable drawable;
                TextDrawable.IShapeBuilder builder = TextDrawable.builder().beginConfig().bold().toUpperCase().endConfig();

                // We want the vibrant palette, if possible, ortherwise darker palettes
                if (vibrantSwatch != null) {
                    drawable = builder.buildRect(fi.getLetter(), vibrantSwatch.getRgb());
                    fi.setColor(vibrantSwatch.getRgb());
                } else if (!swatches.isEmpty()) {
                    drawable = builder.buildRect(fi.getLetter(), swatches.get(0).getRgb());
                    fi.setColor(swatches.get(0).getRgb());
                } else {
                    return;
                }

                fi.setTextDrawable(drawable);
                feedAdapter.notifyDataSetChanged();
            }
        });
    }

    // Converts the difference between two dates into a pretty date
    // There's probably a joke in there somewhere
    public String getPrettyDate(Long time) {

        Date past = new Date(time * 1000);
        Date now = new Date();

        if (TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) > 0) {
            return TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + "d";
        } else if (TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) > 0) {
            return TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + "h";
        }
        if (TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) > 0) {
            return TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + "m";
        } else {
            return TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime()) + "s";
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // We let each fragment create their own options menu
        // That way we can refresh the feeds individually
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_refresh) {
            resetSubmissions();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        MainActivity main = (MainActivity) getActivity();
        if (main == null) {
            return;
        }

        ActionBar ab = main.getSupportActionBar();

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

    // Updates the padding of the header if it's visible
    public void updateHeaderPadding() {
        MainActivity m = ((MainActivity) getActivity());

        // If the fragment comes into view, update padding
        if (m != null) {
            ActionBar ab = m.getSupportActionBar();
            if (ab != null) {
                updateHeaderPadding(ab.isShowing());
            }
        }
    }

    // Updates the padding on header to compensate for what is visible on the screen
    public void updateHeaderPadding(boolean show) {

        if (swipeContainer == null) {
            System.err.println("Can't update padding for swipeContainer, not initialized");
            return;
        }

        if (show) {
            // Padding equivalent to both the toolabr and viewpager
            int height = (int) getResources().getDimension(R.dimen.toolbar_height);
            height += (int) getResources().getDimension(R.dimen.viewpager_height);
            swipeContainer.setPadding(0, height, 0, 0);
            swipeContainer.setProgressViewOffset(true, height, height + 100);
        } else {
            // If we hide the toolbar, we need to reduce the padding to compensate
            int height = (int) getActivity().getApplicationContext().getResources().getDimension(R.dimen.viewpager_height);
            swipeContainer.setPadding(0, height, 0, 0);
            swipeContainer.setProgressViewOffset(true, height, height + 100);
        }
    }

    // When we put a fragment into view, we also need to adjust the padding
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        updateHeaderPadding();
    }

    private boolean toolbarIsShown() {
        // Toolbar is 0 in Y-axis, so we can say it's shown.
        return ((MainActivity) getActivity().getApplicationContext()).findViewById(R.id.toolbar_main).getTranslationY() == 0;
    }

    private boolean toolbarIsHidden() {
        // Toolbar is outside of the screen and absolute Y matches the height of it.
        // So we can say it's hidden.
        View mToolbar = ((MainActivity) getActivity().getApplicationContext()).findViewById(R.id.toolbar_main);
        return mToolbar.getTranslationY() == -mToolbar.getHeight();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Save data
        savedInstanceState.putParcelableArrayList("feedItems", feedAdapter.getFeedItems());
    }
}