package co.adrianblan.cheddar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 2015-07-25.
 */
public class FeedAdapter extends BaseAdapter {

    private ArrayList<FeedItem> feedItems = new ArrayList<FeedItem>();

    public FeedAdapter() {
        //feedItems.add(new FeedItem("Godzilla sighted in New York City, evacuate everyone immediately!", "8hrs + dailymail.com", "143 pts + 87 comments"));
    }

    public void add (FeedItem f){
        feedItems.add(f);
    }

    public void clear() {
        feedItems.clear();
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
    public FeedItem getItem(int position) {
        return feedItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final FeedItem item = feedItems.get(position);

        //TODO get context in constructor
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
        View feed_item = inflater.inflate(R.layout.feed_item, parent, false);

        TextView title = (TextView) feed_item.findViewById(R.id.feed_item_title);
        title.setText(item.getTitle());

        TextView subtitle = (TextView) feed_item.findViewById(R.id.feed_item_subtitle);
        subtitle.setText(item.getSubtitle());

        TextView score = (TextView) feed_item.findViewById(R.id.feed_item_score);
        score.setText(Long.toString(item.getScore()));

        TextView comments = (TextView) feed_item.findViewById(R.id.feed_item_comments);
        comments.setText(Long.toString(item.getCommentCount()));

        TextView time = (TextView) feed_item.findViewById(R.id.feed_item_time);
        time.setText(item.getTime());

        ImageView thumbnail = (ImageView) feed_item.findViewById(R.id.feed_item_thumbnail);
        TextDrawable.IShapeBuilder builder = TextDrawable.builder().beginConfig().bold().toUpperCase().endConfig();

        // If we have a high resolution thumbnail, display it
        if(item.getThumbnail() != null){
            thumbnail.setImageBitmap(item.getThumbnail());
        } else if (item.getTextDrawable() != null) {

            // Otherwise, just use the TextDrawable
            thumbnail.setImageDrawable(item.getTextDrawable());
        }

        // If we click the body, get to the comments
        LinearLayout text = (LinearLayout) feed_item.findViewById(R.id.feed_item_text);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CommentActivity.class);
                Bundle b = new Bundle();
                b.putString("title", item.getTitle());
                b.putSerializable("kids", item.getKids());
                intent.putExtras(b);
                v.getContext().startActivity(intent);
            }
        });

        // If we click the thumbnail, get to the webview
        thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), WebViewActivity.class);
                Bundle b = new Bundle();
                b.putString("title", item.getTitle());
                b.putString("shortUrl", item.getShortUrl());
                b.putString("longUrl", item.getLongUrl());
                intent.putExtras(b);
                v.getContext().startActivity(intent);
            }
        });

        return feed_item;
    }
}
