package co.adrianblan.cheddar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.astuetz.PagerSlidingTabStrip;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setShouldExpand(true);
        tabs.setViewPager(pager);

        enableHttpResponseCache();
        initImageLoader();
    }

    public class PagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"TOP", "ASK", "SHOW", "NEW"};

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {

            FeedFragment ff = FeedFragment.newInstance();
            Bundle b = new Bundle();

            if(position == 0){
                b.putString("url", "/topstories");
            } else if (position == 1) {
                b.putString("url", "/askstories");
            } else if (position == 2) {
                b.putString("url", "/showstories");
            } else if (position == 3) {
                b.putString("url", "/newstories");
            } else {
                b.putString("url", "/topstories");
            }

            ff.setArguments(b);
            return ff;
        }
    }

    // Caches our HTTP responses for up to 1 MB
    // Since each response is less than 1KB we have a lot to spare
    private void enableHttpResponseCache() {
        try {
            long httpCacheSize = 1 * 1024 * 1024; // 1 MiB
            File httpCacheDir = new File(getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
        }
    }

    // Initializes the image loader with what is probably reasonable values
    public void initImageLoader(){

        // Returns if we have already initialized the ImageLoader
        if(ImageLoader.getInstance().isInited()){
            return;
        }

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(getApplication());
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(5 * 1024 * 1024); // 5 MiB
        config.tasksProcessingOrder(QueueProcessingType.FIFO);

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
    }
}
