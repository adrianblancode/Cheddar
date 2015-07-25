package co.adrianblan.cheddar;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Adrian on 2015-07-25.
 */
public class FeedAdapter extends BaseAdapter {

    private ArrayList<FeedItem> feedItems = new ArrayList<FeedItem>();
    private Activity myContext;

    public FeedAdapter() {
        //feedItems.add(new FeedItem("Godzilla sighted in New York City, evacuate everyone immediately!", "8hrs + dailymail.com", "143 pts + 87 comments"));
    }

    public void add (FeedItem f){
        feedItems.add(f);
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //TODO get context in constructor
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
        View feed_item = inflater.inflate(R.layout.feed_item, parent, false);

        TextView title = (TextView) feed_item.findViewById(R.id.feed_item_title);
        title.setText(feedItems.get(position).getTitle());

        TextView subtitle1 = (TextView) feed_item.findViewById(R.id.feed_item_subtitle1);
        subtitle1.setText(feedItems.get(position).getSubtitle1());

        TextView subtitle2 = (TextView) feed_item.findViewById(R.id.feed_item_subtitle2);
        subtitle2.setText(feedItems.get(position).getSubtitle2());

        //TODO image

        return feed_item;
    }
}
