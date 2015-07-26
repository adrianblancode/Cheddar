package co.adrianblan.cheddar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.List;

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

    public FeedItem get (int i){
        return feedItems.get(i);
    }

    public int getPosition(FeedItem f) {

        for(int i = 0; i < getCount(); i++) {
            if(feedItems.get(i).equals(f)){
                return i;
            }
        }

        return -1;
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

        FeedItem item = feedItems.get(position);

        //TODO get context in constructor
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
        View feed_item = inflater.inflate(R.layout.feed_item, parent, false);

        TextView title = (TextView) feed_item.findViewById(R.id.feed_item_title);
        title.setText(feedItems.get(position).getTitle());

        TextView subtitle1 = (TextView) feed_item.findViewById(R.id.feed_item_subtitle1);
        subtitle1.setText(feedItems.get(position).getSubtitle1());

        TextView subtitle2 = (TextView) feed_item.findViewById(R.id.feed_item_subtitle2);
        subtitle2.setText(feedItems.get(position).getSubtitle2());

        ImageView thumbnail = (ImageView) feed_item.findViewById(R.id.feed_item_thumbnail);
        TextDrawable.IShapeBuilder builder = TextDrawable.builder().beginConfig().bold().toUpperCase().endConfig();

        // If we have a high resolution thumbnail, display it
        if(item.getThumbnail() != null){

            thumbnail.setImageBitmap(feedItems.get(position).getThumbnail());

            // If we only have a low resolution favicon, get the dominant color
        } else if(item.getFavicon() != null) {

            Palette myPalette = Palette.generate(item.getFavicon());
            List<Palette.Swatch> swatches = myPalette.getSwatches();
            Palette.Swatch swatch = myPalette.getVibrantSwatch();

            TextDrawable drawable;

            // We want the vibrant palette, if possible, ortherwise darker palettes
            if (swatch != null){
                drawable = builder.buildRect(item.getLetter(), swatch.getRgb());
            } else if(swatches.size() > 1) {
                drawable = builder.buildRect(item.getLetter(), swatches.get(1).getRgb());
            } else {
                drawable = builder.buildRect(item.getLetter(), Color.DKGRAY);
            }

            thumbnail.setImageDrawable(drawable);
        } else {

            // Otherwise we  just display a gray square
            TextDrawable drawable = builder.buildRect(item.getLetter(), Color.DKGRAY);
            thumbnail.setImageDrawable(drawable);
        }

        return feed_item;
    }
}
